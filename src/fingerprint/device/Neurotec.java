package fingerprint.device;

import com.neurotec.biometrics.NBiometricConnection;
import com.neurotec.biometrics.NBiometricOperation;
import com.neurotec.biometrics.NBiometricStatus;
import com.neurotec.biometrics.NBiometricTask;
import com.neurotec.biometrics.NFPosition;
import com.neurotec.biometrics.NFinger;
import com.neurotec.biometrics.NMatchingResult;
import com.neurotec.biometrics.NSubject;
import com.neurotec.biometrics.NTemplate;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.devices.NDeviceManager;
import com.neurotec.devices.NDeviceType;
import com.neurotec.images.NImage;
import com.neurotec.io.NBuffer;
import com.neurotec.util.concurrent.CompletionHandler;
import helpers.ImageHelper;
import helpers.Utils;
import java.util.ArrayList;

import java.util.EnumSet;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.commons.codec.binary.Base64;

public class Neurotec implements IFingerDevice {

    protected final NBiometricClient mBiometricClient;
    protected final NDeviceManager mDeviceManager;
    
    protected NSubject mSubject;
    
    protected final CaptureCompletionHandler mCaptureCompletionHandler;
    
    protected boolean mCapturing;
    
    protected IFingerDeviceEvent mFingerDeviceEvent;
    
    public Neurotec() {
        mBiometricClient = new NBiometricClient();
        mBiometricClient.getRemoteConnections().addToCluster("localhost", 25452, 24932);
        
        mDeviceManager = mBiometricClient.getDeviceManager();
        if (mDeviceManager != null) {
            mDeviceManager.setDeviceTypes(EnumSet.of(NDeviceType.FINGER_SCANNER));
            mDeviceManager.initialize();
        }
        
        NFinger finger = new NFinger();
        
        mSubject = new NSubject();
		mSubject.getFingers().add(finger);
        
        mCaptureCompletionHandler = new CaptureCompletionHandler();
        
        mCapturing = false;
    }
    
    public NBiometricClient getBiometricClient() {
        return mBiometricClient;
    }
    
    @Override
    public void setListener(IFingerDeviceEvent fingerDeviceEvent) {
        mFingerDeviceEvent = fingerDeviceEvent;
    }

    @Override
    public boolean startCapture() {
        boolean isStart = false;
        
        if (!mCapturing) {
            mCapturing = true;
            
            if (mFingerDeviceEvent != null)
                mFingerDeviceEvent.onFingerDeviceStartCapture();
            
            NBiometricTask biometricTask = mBiometricClient.createTask(EnumSet.of(NBiometricOperation.CAPTURE, NBiometricOperation.CREATE_TEMPLATE), mSubject);
            mBiometricClient.performTask(biometricTask, null, mCaptureCompletionHandler);
            
            isStart = true;
        }
        
        return isStart;
    }

    @Override
    public boolean isCapturing() {
        return mCapturing;
    }
    
    @Override
    public boolean stopCapture() {
        boolean isStop = false;
        
        if (mCapturing) {
            mBiometricClient.cancel();
            
            if (mFingerDeviceEvent != null)
                mFingerDeviceEvent.onFingerDeviceStopCapture();
            
            isStop = true;
        }
        
        return isStop;
    }

    public void createTemplateFromImages(final int[] fingerIndexPositions, final String[] fingerBase64Images, final CreateTemplateListener createTemplateListener) {
        NSubject subject = new NSubject();
        
        try {
            for (int fingerBase64ImageIdx = 0; fingerBase64ImageIdx < fingerBase64Images.length; fingerBase64ImageIdx++) {
                NFPosition fingerIndexPosition = Utils.getNFPositionByIndex(fingerIndexPositions[fingerBase64ImageIdx]);
                String fingerBase64Image = fingerBase64Images[fingerBase64ImageIdx];

                byte[] fingerImageBytes = ImageHelper.base64ToBytes(fingerBase64Image);
                if (fingerImageBytes != null) {
                    NBuffer buffer = NBuffer.fromArray(fingerImageBytes);

                    NFinger finger = new NFinger();
                    finger.setPosition(fingerIndexPosition);
                    finger.setImage(NImage.fromMemory(buffer));

                    subject.getFingers().add(finger);
                }
            }
            
            TemplateCreationHandler templateCreationHandler = new TemplateCreationHandler(subject);
            templateCreationHandler.setCreateTemplateListener(createTemplateListener);

            mBiometricClient.createTemplate(subject, null, templateCreationHandler);
        } catch (Exception ex) {
            if (createTemplateListener != null)
                createTemplateListener.onTemplateCreateFailed(ex.getMessage());
        }
    }
    
    public void templateAdd(final int id, final String templateBase64, final TemplateAddListener templateAddListener) {
        try {
            byte[] templateBytes = Base64.decodeBase64(templateBase64);
            NBuffer buffer = NBuffer.fromArray(templateBytes);
            NTemplate template = new NTemplate(buffer);
            
            NSubject subject = new NSubject();
            subject.setTemplate(template);
            subject.setId(String.valueOf(id));
            
            NBiometricTask biometricTask = new NBiometricTask(EnumSet.of(NBiometricOperation.ENROLL));
            biometricTask.getSubjects().add(subject);
            
            TemplateAddHandler templateAddHandler = new TemplateAddHandler(id);
            templateAddHandler.setTemplateAddListener(templateAddListener);
            
            mBiometricClient.performTask(biometricTask, null, templateAddHandler);
        } catch (Exception ex) {
            if (templateAddListener != null)
                templateAddListener.onTemplateAddFailed(ex.getMessage());
        }
    }
    
    public void templateDelete(final int id, final TemplateDeleteListener templateDeleteListener) {
        try {
            NSubject subject = new NSubject();
            subject.setId(String.valueOf(id));
            
            NBiometricTask biometricTask = new NBiometricTask(EnumSet.of(NBiometricOperation.DELETE));
            biometricTask.getSubjects().add(subject);
            
            TemplateDeleteHandler templateDeleteHandler = new TemplateDeleteHandler(id);
            templateDeleteHandler.setTemplateAddListener(templateDeleteListener);
            
            mBiometricClient.performTask(biometricTask, null, templateDeleteHandler);
        } catch (Exception ex) {
            if (templateDeleteListener != null)
                templateDeleteListener.onTemplateDeleteFailed(ex.getMessage());
        }
    }
    
    public void templateIdentify(final String templateBase64, final TemplateIdentifyListener templateIdentifyListener) {
        try {
            byte[] templateBytes = Base64.decodeBase64(templateBase64);
            NBuffer buffer = NBuffer.fromArray(templateBytes);
            NTemplate template = new NTemplate(buffer);
            
            NSubject subject = new NSubject();
            subject.setTemplate(template);
            
            NBiometricTask biometricTask = new NBiometricTask(EnumSet.of(NBiometricOperation.IDENTIFY));
            biometricTask.getSubjects().add(subject);
            
            TemplateIdentifyHandler templateIdentifyHandler = new TemplateIdentifyHandler(subject);
            templateIdentifyHandler.setTemplateAddListener(templateIdentifyListener);
            
            mBiometricClient.performTask(biometricTask, null, templateIdentifyHandler);
        } catch (Exception ex) {
            if (templateIdentifyListener != null)
                templateIdentifyListener.onTemplateIdentifyFailed(ex.getMessage());
        }
    }
    
    protected class CaptureCompletionHandler implements CompletionHandler<NBiometricTask, Object> {

		@Override
		public void completed(final NBiometricTask result, final Object attachment) {
			if (mFingerDeviceEvent != null) {
                NImage image = mSubject.getFingers().get(0).getImage();
                if (image != null)
                    mFingerDeviceEvent.onFingerDeviceImageCaptured(image.toImage());
            }
		}

		@Override
		public void failed(final Throwable th, final Object attachment) {
			
		}

	}
    
    protected class TemplateCreationHandler implements CompletionHandler<NBiometricStatus, Object> {

        protected CreateTemplateListener mCreateTemplateListener;
        protected NSubject mSubject;
        
        public TemplateCreationHandler(final NSubject subject) {
            super();
            
            mSubject = subject;
            mCreateTemplateListener = null;
        }
        
        public void setCreateTemplateListener(final CreateTemplateListener createTemplateListener) {
            mCreateTemplateListener = createTemplateListener;
        }
        
		@Override
		public void completed(final NBiometricStatus result, final Object attachment) {
            if (result == NBiometricStatus.OK) {
                if (mCreateTemplateListener != null)
                    mCreateTemplateListener.onTemplateCreateSuccess(mSubject.getTemplate());
            } else if (result == NBiometricStatus.BAD_OBJECT) {
                if (mCreateTemplateListener != null)
                    mCreateTemplateListener.onTemplateCreateFailed("Finger image quality is too low.");
            } else {
                if (mCreateTemplateListener != null)
                    mCreateTemplateListener.onTemplateCreateFailed("Failed to create template.");
            }
		}

		@Override
		public void failed(final Throwable th, final Object attachment) {
			if (mCreateTemplateListener != null)
                    mCreateTemplateListener.onTemplateCreateFailed(th.getMessage());
		}
	}
    
    protected class TemplateAddHandler implements CompletionHandler<NBiometricTask, Object> {

        protected TemplateAddListener mTemplateAddListener;
        protected int mId;
        
        public TemplateAddHandler(final int id) {
            super();
            
            mId = id;
        }
        
        public void setTemplateAddListener(final TemplateAddListener templateAddListener) {
            mTemplateAddListener = templateAddListener;
        }
        
		@Override
		public void completed(final NBiometricTask task, final Object attachment) {
			if (task.getStatus() == NBiometricStatus.OK) {
				if (mTemplateAddListener != null)
                    mTemplateAddListener.onTemplateAddSuccess(mId);
			} else {
				if (mTemplateAddListener != null)
                    mTemplateAddListener.onTemplateAddFailed(task.getStatus().toString());
			}
		}

		@Override
		public void failed(final Throwable th, final Object attachment) {
			if (mTemplateAddListener != null)
                mTemplateAddListener.onTemplateAddFailed(th.getMessage());
		}
	}
    
    protected class TemplateDeleteHandler implements CompletionHandler<NBiometricTask, Object> {

        protected TemplateDeleteListener mTemplateDeleteListener;
        protected int mId;
        
        public TemplateDeleteHandler(final int id) {
            super();
            
            mId = id;
        }
        
        public void setTemplateAddListener(final TemplateDeleteListener templateDeleteListener) {
            mTemplateDeleteListener = templateDeleteListener;
        }
        
		@Override
		public void completed(final NBiometricTask task, final Object attachment) {
			if (task.getStatus() == NBiometricStatus.OK) {
				if (mTemplateDeleteListener != null)
                    mTemplateDeleteListener.onTemplateDeleteSuccess(mId);
			} else {
				if (mTemplateDeleteListener != null)
                    mTemplateDeleteListener.onTemplateDeleteFailed(task.getStatus().toString());
			}
		}

		@Override
		public void failed(final Throwable th, final Object attachment) {
			if (mTemplateDeleteListener != null)
                mTemplateDeleteListener.onTemplateDeleteFailed(th.getMessage());
		}
	}
    
    protected class TemplateIdentifyHandler implements CompletionHandler<NBiometricTask, Object> {

        protected TemplateIdentifyListener mTemplateIdentifyListener;
        protected NSubject mSubject;
        
        public TemplateIdentifyHandler(final NSubject subject) {
            super();
            
            mSubject = subject;
            mTemplateIdentifyListener = null;
        }
        
        public void setTemplateAddListener(final TemplateIdentifyListener templateIdentifyListener) {
            mTemplateIdentifyListener = templateIdentifyListener;
        }
        
		@Override
		public void completed(final NBiometricTask task, final Object attachment) {
			if ((task.getStatus() == NBiometricStatus.OK) || (task.getStatus() == NBiometricStatus.MATCH_NOT_FOUND)) {
				if (mTemplateIdentifyListener != null) {
                    List<TemplateIdentifyResult> templateIdentifyResultList = new ArrayList<TemplateIdentifyResult>();
                    for (NMatchingResult matchingResult : mSubject.getMatchingResults()) {
                        TemplateIdentifyResult templateIdentifyResult = new TemplateIdentifyResult();
                        templateIdentifyResult.setId(Integer.parseInt(matchingResult.getId()));
                        templateIdentifyResult.setScore(matchingResult.getScore());
                        templateIdentifyResultList.add(templateIdentifyResult);
                    }
                    
                    TemplateIdentifyResult[] templateIdentifyResults = new TemplateIdentifyResult[templateIdentifyResultList.size()];
                    templateIdentifyResultList.toArray(templateIdentifyResults);
                    
                    mTemplateIdentifyListener.onTemplateIdentifySuccess(templateIdentifyResults);
                }
			} else {
				if (mTemplateIdentifyListener != null)
                    mTemplateIdentifyListener.onTemplateIdentifyFailed(task.getStatus().toString());
			}
		}

		@Override
		public void failed(final Throwable th, final Object attachment) {
			if (mTemplateIdentifyListener != null)
                mTemplateIdentifyListener.onTemplateIdentifyFailed(th.getMessage());
		}
	}
    
    public interface CreateTemplateListener {
        void onTemplateCreateSuccess(NTemplate template);
        void onTemplateCreateFailed(String message);
    }
    
    public interface TemplateAddListener {
        void onTemplateAddSuccess(int id);
        void onTemplateAddFailed(String message);
    }
    
    public interface TemplateDeleteListener {
        void onTemplateDeleteSuccess(int id);
        void onTemplateDeleteFailed(String message);
    }
    
    public interface TemplateIdentifyListener {
        void onTemplateIdentifySuccess(TemplateIdentifyResult[] templateIdentifyResults);
        void onTemplateIdentifyFailed(String message);
    }
    
    public static class TemplateIdentifyResult {
        
        protected int mId;
        protected int mScore;
        
        public void setId(final int id) {
            mId = id;
        }
        
        public int getId() {
            return mId;
        }
        
        public void setScore(final int score) {
            mScore = score;
        }
        
        public int getScore() {
            return mScore;
        }
    }
}