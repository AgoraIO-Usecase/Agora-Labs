package io.agora.api.example.examples.audio;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import io.agora.api.example.App;
import io.agora.api.example.R;
import io.agora.api.example.common.TokenGenerator;
import io.agora.api.example.databinding.FragmentAiEchoCancellationBinding;
import io.agora.api.example.utils.AESUtils;
import io.agora.api.example.utils.PermissionUtils;
import io.agora.api.example.utils.SystemUtil;
import io.agora.api.example.utils.ThreadUtils;
import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IAudioFrameObserver;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcConnection;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.RtcEngineEx;
import io.agora.rtc2.audio.AudioParams;
import io.agora.rtc2.video.VideoCanvas;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static io.agora.rtc2.Constants.POSITION_PLAYBACK;

public class AiEchoCancellationFragment extends Fragment implements View.OnClickListener {
    private final String TAG="AgoraLab";

    protected RtcEngineEx rtcEngine;
    protected int sceneMode= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
    private RtcConnection rtcConnection;
    private FragmentAiEchoCancellationBinding binding;
    private String channelName= String.valueOf((int) (Math.random() * 1000));
    private int senderUid =101;
    private int remoteUid=102;

    private SurfaceView remoteView;
    private boolean aiEchoCancellationEnabled;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding= FragmentAiEchoCancellationBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView(){
        binding.ivBack.setOnClickListener(this);
        binding.featureSwitch.setChecked(aiEchoCancellationEnabled);
        binding.featureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SystemUtil.vibrator(getContext());
            aiEchoCancellationEnabled =isChecked;
            updateAiEchoCancellation();
        });
        //binding.wave.setMax((short) 100);
    }

    private void updateAiEchoCancellation(){
        String params;

    }

    @Override public void onStart() {
        super.onStart();
        requestMorePermissions();
    }

    private final String[] PERMISSIONS = new String[]{ Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};
    private final int REQUEST_CODE_PERMISSIONS = 1;
    private void requestMorePermissions() {
        PermissionUtils.checkMorePermissions(getActivity(), PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
            @Override
            public void onHasPermission() {
                start();
            }

            @Override
            public void onUserHasAlreadyTurnedDown(String... permission) {
                PermissionUtils.showExplainDialog(getActivity(),permission, (dialog, which) -> requestPermissions( PERMISSIONS, REQUEST_CODE_PERMISSIONS));
            }

            @Override
            public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                requestPermissions(PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            PermissionUtils.onRequestMorePermissionsResult(getActivity(), PERMISSIONS,
                new PermissionUtils.PermissionCheckCallBack() {
                    @Override
                    public void onHasPermission() {
                        start();
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDown(String... permission) {
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                        PermissionUtils.showToAppSettingDialog(getActivity());
                    }
                });
        }
    }


    private void start(){
        initializeEngine();
        setupSend();
        setupReceiver();
    }

    protected void initializeEngine() {
        if(rtcEngine!=null){
            return;
        }
        RtcEngineConfig config = new RtcEngineConfig();
        config.mContext = getContext().getApplicationContext();
        config.mAppId = AESUtils.getS1(App.getInstance());
        config.mChannelProfile = sceneMode;
        config.mEventHandler = new IRtcEngineEventHandler() {
        };
        config.mAudioScenario = Constants.AudioScenario.getValue(Constants.AudioScenario.DEFAULT);
        config.mAreaCode = ((App)getActivity().getApplication()).getAreaCode();
        try {
            rtcEngine = (RtcEngineEx)RtcEngineEx.create(config);
            rtcEngine.disableAudio();
        } catch (Exception e) {
            e.printStackTrace();
        }
        rtcEngine.enableInEarMonitoring(true);
        rtcEngine.registerAudioFrameObserver(new IAudioFrameObserver() {
            @Override
            public boolean onRecordAudioFrame(String channelId, int type, int samplesPerChannel, int bytesPerSample,
                int channels, int samplesPerSec, ByteBuffer buffer, long renderTimeMs, int avsync_type) {
                return false;
            }

            @Override
            public boolean onPlaybackAudioFrame(String channelId, int type, int samplesPerChannel, int bytesPerSample,
                int channels, int samplesPerSec, ByteBuffer buffer, long renderTimeMs, int avsync_type) {
                //Log.d("AgoraLabs","---onPlaybackAudioFrame----");

                ThreadUtils.runOnUI(() -> {
                    ByteBuffer clone = ByteBuffer.allocate(buffer.capacity());
                    buffer.rewind();//copy from the beginning
                    clone.put(buffer);
                    buffer.rewind();
                    clone.flip();

                    byte[] audioData = clone.array();
                    // 将字节数组转换为 PCM 数据
                    int len = audioData.length / 2;    // 一个样本占两个字节
                    short[] pcmData = new short[len];
                    ByteBuffer.wrap(audioData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(pcmData);

                    // 将 PCM 数据转换为浮点数数组
                    for(int i = 0; i < pcmData.length; i++) {
                        binding.wave.addData(pcmData[i]);
                    }
                    //binding.wave.updateAudioData(pcmData);
                });
                return false;
                //return false;
            }

            @Override
            public boolean onMixedAudioFrame(String channelId, int type, int samplesPerChannel, int bytesPerSample,
                int channels, int samplesPerSec, ByteBuffer buffer, long renderTimeMs, int avsync_type) {
                return false;
            }

            @Override
            public boolean onEarMonitoringAudioFrame(int type, int samplesPerChannel, int bytesPerSample, int channels,
                int samplesPerSec, ByteBuffer buffer, long renderTimeMs, int avsync_type) {
                return false;
            }

            @Override public boolean onPlaybackAudioFrameBeforeMixing(String channelId, int userId, int type,
                int samplesPerChannel, int bytesPerSample, int channels, int samplesPerSec, ByteBuffer buffer,
                long renderTimeMs, int avsync_type, int rtpTimestamp) {
                return false;
            }

            @Override public int getObservedAudioFramePosition() {
                return POSITION_PLAYBACK;
            }

            @Override public AudioParams getRecordAudioParams() {
                int samplesPerCall=(int)(0.01*channelCount*sampleRate);
                return new AudioParams(sampleRate,channelCount,io.agora.rtc2.Constants.RAW_AUDIO_FRAME_OP_MODE_READ_WRITE,samplesPerCall);
            }

            @Override public AudioParams getPlaybackAudioParams() {
                int samplesPerCall=(int)(0.01*channelCount*sampleRate);
                return new AudioParams(sampleRate,channelCount,io.agora.rtc2.Constants.RAW_AUDIO_FRAME_OP_MODE_READ_WRITE,samplesPerCall);
            }


            @Override public AudioParams getMixedAudioParams() {
                return null;
            }

            @Override public AudioParams getEarMonitoringAudioParams() {
                return null;
            }
        });
    }
    protected int sampleRate=48000;
    protected int channelCount=1;

    @Override public void onDestroy() {
        super.onDestroy();
        if(rtcEngine!=null){
            //rtcEngine.stopPreview();
            rtcEngine.leaveChannel();
            RtcEngineEx.destroy();
            rtcEngine=null;
        }
    }

    private void setupSend(){
        rtcConnection=new RtcConnection();
        rtcConnection.channelId=channelName;
        rtcConnection.localUid= senderUid;

        //rtcEngine.enableVideo();
        rtcEngine.enableAudio();

        ChannelMediaOptions mediaOptions=new ChannelMediaOptions();
        mediaOptions.channelProfile= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        mediaOptions.clientRoleType= Constants.CLIENT_ROLE_BROADCASTER;
        mediaOptions.publishMicrophoneTrack = true;
        mediaOptions.publishCameraTrack = false;
        TokenGenerator.INSTANCE.generateToken(rtcConnection.channelId, String.valueOf(rtcConnection.localUid),
            TokenGenerator.TokenGeneratorType.token006, TokenGenerator.AgoraTokenType.rtc,
            token -> {
                joinChannel(token,rtcConnection,mediaOptions,new IRtcEngineEventHandler() {
                    @Override
                    public void onUserJoined(int uid, int elapsed) {
                        //Log.d(TAG, "agora onUserJoined:" + uid);
                    }

                    @Override
                    public void onUserOffline(final int uid, final int reason) {
                        //Log.d(TAG, "onUserOffline:" + uid);
                        ThreadUtils.runOnUI(() -> {
                            if (remoteView != null && remoteView.getParent() != null) {
                                ((ViewGroup) remoteView.getParent()).removeAllViews();
                            }
                        });
                    }

                });
                return null;
            }, exception -> null);
    }

    private void setupReceiver(){
        RtcConnection rtcc=new RtcConnection();
        rtcc.channelId=channelName;
        rtcc.localUid=remoteUid;
        ChannelMediaOptions mediaOptions=new ChannelMediaOptions();
        mediaOptions.channelProfile= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        mediaOptions.clientRoleType= Constants.CLIENT_ROLE_AUDIENCE;
        mediaOptions.publishMicrophoneTrack=false;

        TokenGenerator.INSTANCE.generateToken(rtcc.channelId, String.valueOf(rtcc.localUid),
            TokenGenerator.TokenGeneratorType.token006, TokenGenerator.AgoraTokenType.rtc,
            token -> {
                joinChannel(token,rtcc,mediaOptions,new IRtcEngineEventHandler() {
                    @Override
                    public void onUserJoined(int uid, int elapsed) {
                        ThreadUtils.runOnUI(() -> {
                            remoteView= new SurfaceView(getContext()) ;
                            rtcEngine.setupRemoteVideoEx(new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid),rtcc);
                            addView();
                        });
                    }
                    @Override
                    public void onUserOffline(final int uid, final int reason) {
                        ThreadUtils.runOnUI(() -> {
                            if(remoteView!=null&&remoteView.getParent()!=null){
                                ((ViewGroup)remoteView.getParent()).removeAllViews();
                            }
                        });
                    }
                    @Override
                    public void onRtcStats(RtcStats stats) {
                        ThreadUtils.runOnUI(() -> {
                        });
                    }
                });
                return null;
            }, exception -> null);
    }

    private void joinChannel(String token,RtcConnection connection,ChannelMediaOptions mediaOptions,IRtcEngineEventHandler iRtcEngineEventHandler){
        rtcEngine.joinChannelEx(token, connection, mediaOptions, iRtcEngineEventHandler);
    }

    @Override public void onClick(View v) {
        SystemUtil.vibrator(getContext());
        if(v.getId()== R.id.iv_back) {
            Navigation.findNavController(v).popBackStack();
        }
    }



    private void addView(){

    }

}
