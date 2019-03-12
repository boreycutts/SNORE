package com.coreybutts.snore;

import java.io.Serializable;

/**
 * Created by Borey on 2/14/2019.
 */

public class XYValue implements Serializable
{
    private double x;
    private double y;

    public XYValue(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }
}
