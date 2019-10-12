package com.dji.ux.sample.functions;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.mission.timeline.actions.MissionAction;

import static com.dji.ux.sample.functions.Common.getFlightAssistant;
import static com.dji.ux.sample.functions.Common.getFlightController;

public class myMissionAction extends MissionAction {
        MediaPlayer beepSoundMediaPlayer = null;
        int sleepTime;
        Context context;
        static TextToSpeech mTTS;
        static Boolean aborted = false;
        public myMissionAction(long delay, Context contxt) {
            aborted = false;
            sleepTime=((int)(delay+5)/10)*10;
            context=contxt;
            if (mTTS==null) {
                mTTS = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            int result = mTTS.setLanguage(Locale.ENGLISH);

                            if (result == TextToSpeech.LANG_MISSING_DATA
                                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Log.e("TTS", "Language not supported");
                            } else {
                            }
                        } else {
                            Log.e("TTS", "Initialization failed");
                        }

                    }
                });
            }

        }

        //MediaPlayer.

        public void run() {
            startRun();
            getFlightAssistant().setLandingProtectionEnabled(false, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError!=null)
                        System.out.println("djiError Landing protection " + djiError);
                }
            });
            /*
            getFlightController().setCinematicModeEnabled(true, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError!=null)
                        System.out.println("setCinematicModeEnabled " + djiError);
                }
            });
            */
            /*
            getFlightController().setSmartReturnToHomeEnabled(true, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError!=null)
                        System.out.println("djiError RTH " + djiError);
                }
            });
            */

            Log.d("myDelay", "start");
            long loopDelay=10;
            long loopTimes = sleepTime/loopDelay;
            //beep(R.raw.beep);
            try{
                for (long i=0; i<loopTimes; ++i){
                    Log.d("myDelay", "sleeping " + loopDelay);
                    long timeLeft = (sleepTime-i*loopDelay);
                    userMessage((int)timeLeft);
                    TimeUnit.SECONDS.sleep(loopDelay);
                    if (aborted) {
                        mTTS.speak("Aborted", TextToSpeech.QUEUE_FLUSH, null);

                        return;
                    }

                }
                showToast("Starting");

                if (aborted) {
                    mTTS.speak("Aborted", TextToSpeech.QUEUE_FLUSH, null);
                    return;
                }

            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            Log.d("myDelay", "stop");
            getFlightController().startPrecisionTakeoff(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    finishRun(null);
                    if (djiError!=null)
                        System.out.println("djiError startPrecisionTakeoff " + djiError);
                }
            });


        }

        static public void abort(){
            aborted=true;
        }

    private void userMessage(int timeLeft) {
            String message;
        if (timeLeft%30==0) {
            int minute=timeLeft/60;
            int seconds = timeLeft%60;
            if (seconds==0)
                message = minute + " minutes";
            else if (minute>0)
                message = minute + " minutes and " + seconds + " seconds";
            else
                message = seconds + " seconds";
            message = message + " to launch";

            if (timeLeft%60==0) {
                mTTS.setSpeechRate((float) 0.8);
                mTTS.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            }
            showToast(message );
            //beep(R.raw.beep);
        }
        if (timeLeft==8) {
            //beep(R.raw.countdown);
            /*
            getFlightController().turnOnMotors( new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError!=null)
                        System.out.println("turn on motors " + djiError);
                }
            });
            void setLandingProtectionEnabled

            */
            beepSoundMediaPlayer.release();
        }

    }

    private void beep(int sound) {
        if  (beepSoundMediaPlayer==null)
            beepSoundMediaPlayer = MediaPlayer.create(context, sound);
        beepSoundMediaPlayer.start();
    }


    private void showToast(final String toastMsg) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show();
            }
        });
    }


        public boolean isPausable() {
            return false;
        }

        public synchronized void pause() {
        }

        public synchronized void resume() {
        }

        public synchronized void stop() {
        }

        public DJIError checkValidity() {
            return null;
        }


        protected void getCacheKeyValue() {
        }

        protected void startListen() {
        }

        protected void stopListen() {
        }

    }
