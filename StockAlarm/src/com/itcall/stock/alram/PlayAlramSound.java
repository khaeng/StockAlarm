package com.itcall.stock.alram;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class PlayAlramSound implements Runnable {

	private static final int SINGLE_POOL_SIZE = 1;

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(SINGLE_POOL_SIZE);

	ScheduledFuture<?> future;
	int sel = 1; // 홀/짝수를 통해 비프음 변경을 위한 변수
	int repeat = 0; // 비프음 반복 체크

	public PlayAlramSound(int repeat) {
		this.sel = repeat;
		this.repeat = repeat;
	}

	public static void playAlramSound(int count) {
		PlayAlramSound sound = new PlayAlramSound(count);
		sound.future = scheduler.scheduleAtFixedRate(sound, 1, 1, TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {
		if(repeat <= 0) {
			future.cancel(false);
		} else {
			beepSound(sel); // 실행
		}
	}

	public void beepSound(int num) {

		try {
			// 홀/짝 구분 해서 음을 변경
			double snd = num % 2 + 1;
			byte[] buf = new byte[1];
			
			// 이동음 반복 실행시 이동음 높낮이/길이 변경
			AudioFormat af = new AudioFormat((float) 44100, 8, 1, true, false);
			/*
	    	AudioFormat( //오디오 재생형식을 지정
	     		float sampleRate, //1초당 샘플(재생) 수
	     		int sampleSizeInBits,  //각 샘플의 비트수
	     		int channels, //이 형식의 오디오 채널 수
	     		boolean signed, //데이터가 부호 첨부인지 부호 없음인지 나타냄
	     		boolean bigEndian) //단일 샘플 데이터를 big Endian 의 바이트 순서로 저장 할지 little Endian 바이트 순서로 저장 할지 정함
			 */

			SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
			//SourceDataLine은 데이터를 기입 할 수 있는 데이터 선이다. 믹서에 소스 역할을 한다.
			//응용 프로그램은 바이트 버퍼링을 처리하고 믹서에 전달 소스 데이터 라인, 오디오 바이트를 기록한다.
			//믹서는 샘플을 사운드 카드 오디오 출력 장치를 통해 나타낼 수 있다.

			sdl.open();
			sdl.start();

			// 반복 숫자에 따라 주파주 재설정 후 비프음 출력 ( 모르겠다 이부분 )
			for (int i = 0; i < 100 / repeat * (float) 44100 / 1000; i++) {
				double angle = i / ((float) 44100 / 440) * snd * (1 + repeat) * Math.PI;
				buf[0] = (byte) (Math.sin(angle) * 100);
				sdl.write(buf, 0, 1);
			}

			sdl.drain();
			sdl.stop();
		} catch (Exception e) {

		}
		repeat--; // 남은 소리 재생 횟수 감수

	}

}
