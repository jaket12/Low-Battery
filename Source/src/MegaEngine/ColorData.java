package MegaEngine;


public class ColorData 
{
    
    private int[] Red;
    private int[] Green;
    private int[] Blue;
    private float[] Transparency;
    private int Layers;
    
    public ColorData(int layers)
    {
        Layers = layers;
        Red = new int[Layers];
        Green = new int[Layers];
        Blue = new int[Layers];
        Transparency = new float[Layers];
    }
    
    public void Red(int layer, int value)
    {
        Red[layer] = value;
    }

    public void Green(int layer, int value)
    {
        Green[layer] = value;
    }
    
    public void Blue(int layer, int value)
    {
        Blue[layer] = value;
    }
    
    public void Transparency(int layer, int value)
    {
        Transparency[layer] = value;
    }

    public int Red(int layer)
    {
        return Red[layer];
    }
    
    public int Green(int layer)
    {
        return Green[layer];
    }
    
    public int Blue(int layer)
    {
        return Blue[layer];
    }
    
    public float RedF(int layer)
    {
        return Red[layer] / 256f;
    }
    
    public float GreenF(int layer)
    {
        return Green[layer] / 256f;
    }
    
    public float BlueF(int layer)
    {
        return Blue[layer] / 256f;
    }
    
    public float Transparency(int layer)
    {
        return Transparency[layer];
    }
    
    public void setLayerScheme(int layer, int red, int green, int blue, float trans)
    {
        Red[layer] = red;
        Green[layer] = green;
        Blue[layer] = blue;
        Transparency[layer] = trans;
    }
    
    public void setLayerScheme(int layer, float red, float green, float blue, float trans)
    {
        int value;
        if (red >= 1)
        {
            value = 255;
        }
        else
        {
            value = (int)(red * 256);
        }
        Red[layer] = value;
        
        if (green >= 1)
        {
            value = 255;
        }
        else
        {
            value = (int)(green * 256);
        }
        Green[layer] = value;
        
        if (blue >= 1)
        {
            value = 255;
        }
        else
        {
            value = (int)(blue * 256);
        }
        Blue[layer] = value;
        
        Transparency[layer] = trans;
    }
    
    public int getLayerCount()
    {
        return Layers;
    }
}
