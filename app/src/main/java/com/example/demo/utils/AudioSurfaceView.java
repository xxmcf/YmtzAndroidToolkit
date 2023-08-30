package com.example.demo.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.demo.R;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import androidx.annotation.NonNull;
import androidx.core.util.LogWriter;

import javax.security.auth.login.LoginException;

public class AudioSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private final String TAG = AudioSurfaceView.class.getSimpleName();

    //波形线的画笔
    private Paint wavePaint;
    private SurfaceHolder holder;

    private Thread drawThread=null;
    private boolean isRunning=true;

    private final int MAX_BLOCK_NUM = 18;
    private final int MARGIN_RIGHT=30;
    //上下基线使用的高度
    private final int MARKER_LINE=40;
    //标识圆的半径
    private final int RADIOUS=MARKER_LINE/4;
    private int MAX_WIDHT=getWidth()-MARGIN_RIGHT;
    //绘制最大的高度
    private int MAX_HEIGHT=getHeight()-MARKER_LINE;
    private int samplerRate=8000;
    //采样位宽,默认16bit
    private int bitWidth=16;
    private final int SECOND_PRESCREEN=20;
    //每个屏幕绘制多少个采样点
    private float drawSamplerCountPreScreen = (float)(samplerRate/Constant.INDEX_TIMES*SECOND_PRESCREEN);
    //每隔多远描绘一次，默认一个屏幕只描绘20s*16000/100次
    private float divider = (float) (MAX_WIDHT/drawSamplerCountPreScreen);
    //y轴缩放的倍数,默认16bit所以是2个字节
    private float yAxisTimes = (float)(Short.MAX_VALUE/ MAX_HEIGHT);
    private long previousTime=0;
    //两次绘图间隔的时间
    private final int DRAW_TIME = 5;//1000 / 200;
    private boolean  rightChannel=false;
    private boolean isFullScreen=false;

    //缓冲区数据
    private LinkedList<Integer> inBuf = new LinkedList<Integer>();
    private LinkedList<Float> floatInBuf = new LinkedList<Float>();

    public AudioSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.holder = getHolder();
        holder.addCallback(this);
        initView();
    }

    private void initView() {
        wavePaint = new Paint();
        wavePaint.setColor(getResources().getColor(R.color.waveformCenterLine));
        wavePaint.setStrokeWidth(1/2);
        wavePaint.setAntiAlias(true);
        wavePaint.setFilterBitmap(true);
        wavePaint.setStyle(Paint.Style.FILL);
    }

    private void startDraw() {
        if (drawThread == null || !isRunning) {
            isRunning = true;
            drawThread = new Thread() {
                @Override
                public void run() {
                    while(isRunning) {
                        handleDrawAudioWave();
                        //handleDrawAudioWaveInt();
                    }
                    Log.i(TAG, "Draw thread exit.");
                }
            };
            drawThread.start();
        }
    }

    public void stopDraw() {
        Log.i(TAG, "stopDraw.");
        if (drawThread != null || isRunning) {
            isRunning = false;
            if (!inBuf.isEmpty()) {
                inBuf.clear();
            }
            drawThread = null;
        }
    }

    public void setAudioParam(int samplerRate, int bitWidth, boolean rightChannel){
        this.rightChannel=rightChannel;
        this.samplerRate=samplerRate;
        this.bitWidth=bitWidth;
        this.MAX_WIDHT=getWidth()-MARGIN_RIGHT;
        this.MAX_HEIGHT=getHeight()-MARKER_LINE;
        this.drawSamplerCountPreScreen = (float) (samplerRate/Constant.INDEX_TIMES*SECOND_PRESCREEN);
        this.divider = (float) (MAX_WIDHT/drawSamplerCountPreScreen);
        this.yAxisTimes = getYAxisTimesByBitWidth(bitWidth);
        startDraw();
    }

    public void addAudioData(byte[] data,int size){
        setAudioParam(samplerRate, bitWidth, false);
        getTargetAudioBuf(calculateAmplitudesByChunks(data));
        //getTargetAudioBuf(byteArray2SamplerArray(data, size));
    }

    private final int BIT_8_WIDTH = 8;
    private final int BIT_16_WIDTH = 16;
    private final int BIT_24_WIDTH = 24;
    private final int BIT_32_WIDTH = 32;
    private float getYAxisTimesByBitWidth(int bitWidth){
        float tempYAxisTimes=0;
        switch (bitWidth){
            case BIT_8_WIDTH:
                tempYAxisTimes=(float)((float)Byte.MAX_VALUE/(float)MAX_HEIGHT);
                break;
            case BIT_16_WIDTH:
                tempYAxisTimes=(float)((float)Short.MAX_VALUE/(float) MAX_HEIGHT);
                break;
            case BIT_24_WIDTH:
                tempYAxisTimes=(float)((float)8388607/(float)MAX_HEIGHT);
                break;
            case BIT_32_WIDTH:
                tempYAxisTimes=(float)((float)Integer.MAX_VALUE/(float)MAX_HEIGHT);
                break;
        }
        return tempYAxisTimes;
    }

    private int[] byteArray2SamplerArray(byte[] data,int size){
        int byteCountPreSampler = (bitWidth /8);
        if (data==null || size==0){
            return null;
        }
        int samplerCount=0;
        if (size% byteCountPreSampler==0){
            samplerCount =  size/byteCountPreSampler;
        }else{
            samplerCount =  size/byteCountPreSampler+1;
        }
        if (samplerCount==0){
            return null;
        }
        int tempData =0;
        int[] tempSamplerData=new int[samplerCount];

        int j=0;
        for(int i=0;i<samplerCount;i++){
            tempData=0;
            for(int k=0;k<byteCountPreSampler;k++){
                int tempBuf =0;
                if ((j+k)<data.length){
                    tempBuf = ( data[j+k] << (k*8) );
                }
                tempData = (tempData | tempBuf);
            }
            tempSamplerData[i]=tempData;
            j+=byteCountPreSampler;
        }
        return tempSamplerData;
    }

    private void getTargetAudioBuf(int[] tempBuf){
        synchronized (inBuf) {
            if (isRunning){
                for (int i = 0; i < tempBuf.length; i += Constant.INDEX_TIMES) {
                    if (rightChannel){
                        if( (i+1) <tempBuf.length ){
                            inBuf.add(tempBuf[i+1]);
                        }
                    }else{
                        inBuf.add(tempBuf[i]);
                    }
                }

            }else{
                if (!inBuf.isEmpty()){
                    inBuf.clear();
                }
            }
        }
    }

    private void getTargetAudioBuf(float[] tempBuf){
        synchronized (floatInBuf) {
            for (int i = 0; i < tempBuf.length; i += 1) {
                if (isRunning){
                    if (rightChannel){
                        if( (i+1) <tempBuf.length ){
                            floatInBuf.add(tempBuf[i+1]);
                        }
                    }else{
                        floatInBuf.add(tempBuf[i]);
                    }
                }else{
                    if (!floatInBuf.isEmpty()){
                        floatInBuf.clear();
                    }
                    break;
                }
            }
            Log.i(TAG, "getTargetAudioBuf, length=" + floatInBuf.size());
        }
    }

    public static float[] calculateAmplitudesByChunks(byte[] buffer) {
        int chunkSize = 160;
        int numChunks = (buffer.length + chunkSize - 1) / chunkSize;
        float[] amplitudes = new float[numChunks];

        for (int i = 0; i < numChunks; i++) {
            int startIdx = i * chunkSize;
            int endIdx = Math.min(startIdx + chunkSize, buffer.length);

            float chunkSum = 0.0f;
            for (int j = startIdx; j < endIdx; j += 2) {
                short sample = (short) ((buffer[j] & 0xFF) | (buffer[j + 1] << 8));
                //float normalizedValue = (float)sample / Short.MAX_VALUE;
                float normalizedValue = (float)sample;
                chunkSum += Math.abs(normalizedValue);
            }
            float averageAmplitude = chunkSum / ((endIdx - startIdx) / 2);
//            if (averageAmplitude < 0.01f) {
//                averageAmplitude = 0;
//            }
            amplitudes[i] = averageAmplitude;

        }

        return amplitudes;
    }

    private void sleep(int msecs) {
        try {
            Thread.sleep(msecs);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleDrawAudioWave() {
        if (floatInBuf.isEmpty()){
            sleep(5);
            return;
        }
        long currentTime = new Date().getTime();
        if(true) {
            float[] buf = new float[MAX_BLOCK_NUM];
            LinkedList<Float> buf2 = new LinkedList<Float>();
            synchronized (floatInBuf) {
                if (floatInBuf.size() < MAX_BLOCK_NUM)
                    return;
                int j = 0;
                int size = floatInBuf.size()/MAX_BLOCK_NUM * MAX_BLOCK_NUM;
                for (int i = 0; i < size; i++) {
                    buf2.add(floatInBuf.get(i));
                    j++;
                    if (j == MAX_BLOCK_NUM) {
                        j = 0;
                        drawWaveForm2(buf2);
                        buf2.clear();
//                        sleep(1);
                    }
                }
//                Iterator iterator=floatInBuf.iterator();
//                while(floatInBuf.size() > drawSamplerCountPreScreen){
//                    if (iterator.hasNext()){
//                        iterator.next();
//                        iterator.remove();
//                    }
//                }
//                buf2 = (LinkedList<Float>) floatInBuf.clone();// 保存
                floatInBuf.clear();
//                if (floatInBuf.size() == size) {
//                    floatInBuf.clear();
//                } else {
//                    for (int i = 0; i < size; i++) {
//                        floatInBuf.remove(0);
//                    }
//                }
            }
            //drawWaveForm2(buf2);
            previousTime = new Date().getTime();
        }
    }

    private void handleDrawAudioWaveInt() {
        if (inBuf.isEmpty()){
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
        long currenTime = new Date().getTime();
        if(currenTime - previousTime >= DRAW_TIME){
            LinkedList<Integer> buf = new LinkedList<Integer>();
            synchronized (inBuf) {
                if (inBuf.size() == 0)
                    return;
                Iterator iterator=inBuf.iterator();
                while(inBuf.size() > drawSamplerCountPreScreen){
                    if (iterator.hasNext()){
                        iterator.next();
                        iterator.remove();
                    }
                }
				/*while(inBuf.size() > drawSamplerCountPreScreen){
					inBuf.remove(0);
				}*/
                buf = (LinkedList<Integer>) inBuf.clone();// 保存
                //inBuf.clear();
            }
            drawWaveForm(buf);
            previousTime = new Date().getTime();
        }
    }

    private void drawWaveForm(LinkedList<Integer> buf) {
        Canvas canvas= holder.lockCanvas();// 关键:获取画布
//        wavePaint.setStrokeWidth(5);

        if(canvas==null){
            return;
        }

        initBaseView(canvas);
        float baseLineX =(float) (buf.size()* divider);

        if(getWidth() - baseLineX <= MARGIN_RIGHT){//如果超过预留的右边距距离
            baseLineX = MAX_WIDHT;//画的位置x坐标
        }

//        canvas.drawCircle(baseLineX, RADIOUS, RADIOUS, wavePaint);// 上面小圆
//        canvas.drawCircle(baseLineX, getHeight()-RADIOUS, RADIOUS, wavePaint);// 下面小圆
//        canvas.drawLine(baseLineX, RADIOUS*2, baseLineX, getHeight()-RADIOUS*2, wavePaint);//垂直的线
        int upMinHeight=MARKER_LINE/2;
        int underMaxHeight=getHeight()-upMinHeight;
        int rectWidth = 9;
        int span = 6;

        for (int i = 0; i <buf.size() ; i++) {
            if(isRunning){
                int bufData=buf.get(i);
                float y=0;
                if(bufData==0){
                    y=getHeight()/2;// 调节缩小比例，调节基准线
                }else{
                    y =bufData/MAX_HEIGHT + getHeight()/2;
                }

                float x=(i) * divider;

                if(x >= MAX_WIDHT){
                    x = MAX_WIDHT;
                }
                if(y<=upMinHeight){
                    y = upMinHeight;
                }
                if(y>underMaxHeight){
                    y = underMaxHeight;
                }
                float y1 = getHeight() - y;

                if(y1<=upMinHeight){
                    y1 = upMinHeight;
                }
                if(y1>underMaxHeight){
                    y1 = underMaxHeight;
                }

                Log.e(TAG,"buf.size=" + buf.size() + ",i="+i+",[" +x + ","+y+","+x+","+y1+"]");

                canvas.drawLine(x, y,  x ,y1, wavePaint);//中间出波形
            }else{
                break;
            }

        }
        holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
    }

    private void drawWaveForm2(LinkedList<Float> buf) {
        Canvas canvas= holder.lockCanvas();// 关键:获取画布
//        wavePaint.setStrokeWidth(5);

        if(canvas==null){
            return;
        }

        initBaseView(canvas);
        float baseLineX =(float) (buf.size()* divider);

        if(getWidth() - baseLineX <= MARGIN_RIGHT){//如果超过预留的右边距距离
            baseLineX = MAX_WIDHT;//画的位置x坐标
        }

//        canvas.drawCircle(baseLineX, RADIOUS, RADIOUS, wavePaint);// 上面小圆
//        canvas.drawCircle(baseLineX, getHeight()-RADIOUS, RADIOUS, wavePaint);// 下面小圆
//        canvas.drawLine(baseLineX, RADIOUS*2, baseLineX, getHeight()-RADIOUS*2, wavePaint);//垂直的线
        int upMinHeight=MARKER_LINE/2;
        int underMaxHeight=getHeight()-upMinHeight;
        int rectWidth = 9;
        int span = 6;
        int offsetX = (getWidth() - (rectWidth+span)*buf.size()) / 2;

        for (int i = 0; i <buf.size() ; i++) {
            if(isRunning){
                float bufData=buf.get(i);
                float y=0;
                if(bufData==0){
                    y=getHeight()/2;// 调节缩小比例，调节基准线
                }else{
                    y =bufData/yAxisTimes + getHeight()/2;
                }

                float x=(i) * (rectWidth+span) + offsetX;

                if(x >= MAX_WIDHT){
                    x = MAX_WIDHT;
                }
                if(y<=upMinHeight){
                    y = upMinHeight;
                }
                if(y>underMaxHeight){
                    y = underMaxHeight;
                }
                float y1 = getHeight() - y;

                if(y1<=upMinHeight){
                    y1 = upMinHeight;
                }
                if(y1>underMaxHeight){
                    y1 = underMaxHeight;
                }

                //Log.e(TAG,"buf.size=" + buf.size() + ",i="+i+",[" +x + ","+y+","+x+","+y1+"]");

                canvas.drawRect(x, y,  x+rectWidth ,y1, wavePaint);//中间出波形
            }else{
                break;
            }

        }
        holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
    }

    private void drawWaveForm(float[] buf) {
        Canvas canvas= holder.lockCanvas();// 关键:获取画布
        wavePaint.setStrokeWidth(5);

        if(canvas==null){
            return;
        }

        initBaseView(canvas);
//        float baseLineX =(float) (buf.length* divider);

//        if(getWidth() - baseLineX <= MARGIN_RIGHT){//如果超过预留的右边距距离
//            baseLineX = MAX_WIDHT;//画的位置x坐标
//        }

//        canvas.drawCircle(baseLineX, RADIOUS, RADIOUS, wavePaint);// 上面小圆
//        canvas.drawCircle(baseLineX, getHeight()-RADIOUS, RADIOUS, wavePaint);// 下面小圆
//        canvas.drawLine(baseLineX, RADIOUS*2, baseLineX, getHeight()-RADIOUS*2, wavePaint);//垂直的线
        int upMinHeight=MARKER_LINE/2;
        int underMaxHeight=getHeight()-upMinHeight;
        int rectWidth = 9;
        int span = 6;
        int offsetX = (getWidth() - (rectWidth+span)*buf.length) / 2;

        for (int i = 0; i <buf.length ; i++) {
            if(isRunning){
                float bufData=buf[i];
                float y=0;
                if(bufData==0){
                    y=getHeight()/2;// 调节缩小比例，调节基准线
                }else{
                    y =bufData*MAX_HEIGHT + getHeight()/2;
                }

                float x=(i) * (rectWidth+span) + offsetX;

                if(x >= MAX_WIDHT){
                    x = MAX_WIDHT;
                }
                if(y<=upMinHeight){
                    y = upMinHeight;
                }
                if(y>underMaxHeight){
                    y = underMaxHeight;
                }
                float y1 = getHeight() - y;

                if(y1<=upMinHeight){
                    y1 = upMinHeight;
                }
                if(y1>underMaxHeight){
                    y1 = underMaxHeight;
                }

                //Log.e(TAG,"buf.size=" + buf.length + ",i="+i+",bufData="+bufData+",[" +x + ","+y+","+x+","+y1+"]");

                canvas.drawRect(x, y,  x+rectWidth ,y1, wavePaint);//中间出波形
            }else{
                break;
            }

        }
        holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
    }


    public void surfaceCreated(@NonNull SurfaceHolder var1) {
        Log.i(TAG, "surfaceCreated");
        initSurfaceView();
    }

    private void initSurfaceView(){
        //初始化基础view
        Canvas canvas = holder.lockCanvas();
        if(canvas==null){
            return;
        }
        initBaseView(canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    private void initBaseView(Canvas canvas){
        if (canvas==null){
            return;
        }
        canvas.drawColor(getResources().getColor(R.color.waveformBacg));

        Paint borderLine =new Paint();
        borderLine.setColor(getResources().getColor(R.color.waveformBorderLine));
        //最上面的那根线
        canvas.drawLine(0, MARKER_LINE/2, getWidth(), MARKER_LINE/2, borderLine);
        //最下面的那根线
        canvas.drawLine(0, getHeight()-MARKER_LINE/2-1,getWidth(), getHeight()-MARKER_LINE/2-1, borderLine);

        Paint centerLine =new Paint();
        centerLine.setColor(getResources().getColor(R.color.waveformCenterLine ));
        //中心线
        canvas.drawLine(0, getHeight()/2, getWidth() ,getHeight()/2, centerLine);

        canvas.drawCircle(0, RADIOUS, RADIOUS, wavePaint);// 上面小圆
        canvas.drawCircle(0, getHeight()-RADIOUS, RADIOUS, wavePaint);// 下面小圆
        canvas.drawLine(0, RADIOUS*2, 0, getHeight()-RADIOUS*2, wavePaint);//垂直的线

    }

    public void surfaceChanged(@NonNull SurfaceHolder var1, int var2, int var3, int var4) {
        Log.i(TAG, "surfaceChanged");
    }

    public void surfaceDestroyed(@NonNull SurfaceHolder var1) {
        Log.i(TAG, "surfaceDestroyed");
        stopDraw();
    }

}
