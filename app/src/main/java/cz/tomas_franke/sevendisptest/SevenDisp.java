package cz.tomas_franke.sevendisptest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class SevenDisp extends View {

    private final int MIN_WIDTH  = 16;
    private final int MIN_HEIGHT = 20;

    private final int polx[][] = {
            { 1, 2, 8, 9, 8, 2},    //Segment 0
            { 9,10,10, 9, 8, 8},    //Segment 1
            { 9,10,10, 9, 8, 8},    //Segment 2
            { 1, 2, 8, 9, 8, 2},    //Segment 3
            { 1, 2, 2, 1, 0, 0},    //Segment 4
            { 1, 2, 2, 1, 0, 0},    //Segment 5
            { 1, 2, 8, 9, 8, 2},    //Segment 6
    };
    private final int poly[][] = {
            { 1, 0, 0, 1, 2, 2},    //Segment 0
            { 1, 2, 8, 9, 8, 2},    //Segment 1
            { 9,10,16,17,16,10},    //Segment 2
            {17,16,16,17,18,18},    //Segment 3
            { 9,10,16,17,16,10},    //Segment 4
            { 1, 2, 8, 9, 8, 2},    //Segment 5
            { 9, 8, 8, 9,10,10},    //Segment 6
    };
    private final byte decode[] =  {
            0x3F, 0x06, 0x5B, 0x4F,
            0x66, 0x6D, 0x7D, 0x07,
            0x7F, 0x6F, 0x77, 0x7C,
            0x39, 0x5E, 0x79, 0x71
    };

    private byte seg;           // segments state
    private boolean dec;        // is decimal point visible?
    private boolean left;       // is decimal point at left?
    private int on;             // On color
    private int off;            // Off color
    private byte map[];         // how segments are mapped to databits

    private Paint p;
    private Path ph;

    public SevenDisp(Context context) {
        super(context);
        init(null, 0);
    }

    public SevenDisp(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SevenDisp(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SevenDisp, defStyle, 0);

        dec = a.getBoolean(R.styleable.SevenDisp_showPoint,true);
        left = a.getBoolean(R.styleable.SevenDisp_isLeft,false);
        off = a.getColor(R.styleable.SevenDisp_offColor,Color.DKGRAY);
        on = a.getColor(R.styleable.SevenDisp_onColor,Color.DKGRAY);
        seg = (byte) a.getInteger(R.styleable.SevenDisp_initState,0);

        a.recycle();

        map = new byte[8];
        for(byte i=0; i<8; i++)
            map[i] = i;

        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.FILL);

        ph = new Path();

        invalidate();
    }

    @Override
    protected int getSuggestedMinimumWidth() { return MIN_WIDTH; }

    @Override
    protected int getSuggestedMinimumHeight() {
        return MIN_HEIGHT;
    }
    
    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        int dx = getWidth() / MIN_WIDTH;
        int dy = getHeight() / MIN_HEIGHT;

        for(int i=0,m=1; i<7; i++,m<<=1) {
            ph.reset();
            p.setColor(((seg & m)!=0) ? on : off);

            ph.moveTo(dx*polx[i][0]+(3*dx), dy*poly[i][0]+dy);
            ph.lineTo(dx*polx[i][1]+(3*dx), dy*poly[i][1]+dy);
            ph.lineTo(dx*polx[i][2]+(3*dx), dy*poly[i][2]+dy);
            ph.lineTo(dx*polx[i][3]+(3*dx), dy*poly[i][3]+dy);
            ph.lineTo(dx*polx[i][4]+(3*dx), dy*poly[i][4]+dy);
            ph.lineTo(dx*polx[i][5]+(3*dx), dy*poly[i][5]+dy);
            ph.lineTo(dx*polx[i][0]+(3*dx), dy*poly[i][0]+dy);

            c.drawPath(ph, p);
        }
        if (dec) {
            p.setColor(((seg & 0x80)!=0) ? on : off);  // DP
            if (left)
                c.drawCircle(dx*2-(dx/2), dy*17+(dy/2), dx, p);
//                c.drawOval((dx, dy*17+(dy/2), dx*3/2, dy*3/2, p);
            else
//                c.drawOval((dx*14-(dx/2), dy*17+(dy/2), dx*3/2, dy*3/2, p);
                c.drawCircle(dx*15-(dx/2), dy*17+(dy/2), dx, p);
        }

    }

    public void setOnColor(int c) {
        if (c==on) return;
        on = c;
        invalidate();
    }

    public void setOffColor(int c) {
        if (c==off) return;
        off = c;
        invalidate();
    }

    public void setPointVisible(boolean visible) {
        if (visible==dec) return;
        dec = visible;
        invalidate();
    }

    public void setPointLeft(boolean isleft) {
        if (isleft==left) return;
        left = isleft;
        invalidate();
    }

    public void setSegmentMap(byte sgmp[]) {
        map = sgmp;
    }

    public void setSegments(byte segments) {
        byte mask = 0x01;
        seg = 0;
        for(byte n=0; n<8; n++) {
            if ((segments & mask) != 0) {
                seg |= (1 << map[n]);
            }
            mask <<= 1;
        }
        invalidate();
    }

    public void Clear() {
        if (seg==0) return;
        seg = 0;
        invalidate();
    }

    public void Disp(int x) {
        byte segm = (byte) (seg & 0x80);
        segm |= decode[x & 0xf];
        if (segm==seg) return;
        seg = segm;
        invalidate();
    }

    public void setDP(boolean state) {
        boolean b = ((seg & 0x80)!=0);
        if (state==b) return;
        if (state)
            seg |= 0x80;
        else
            seg &= 0x7f;
        invalidate();
    }
}