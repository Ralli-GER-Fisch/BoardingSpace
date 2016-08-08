package de.boardingspace.ralligerfisch.util;
public class TexturedVertex {
    // Vertex data
    private float[] xyzw = new float[] {0f, 0f, 0f, 1f};
    private float[] rgba = new float[] {1f, 1f, 1f, 1f};
    private float[] uv = new float[] {0f, 0f};
    private int index = 0;
     
    // The amount of bytes an element has
    public static final int elementBytes = 4;
     
    // Elements per parameter
    public static final int positionElementCount = 4;
    public static final int colorElementCount = 4;
    public static final int textureElementCount = 2;
     
    // Bytes per parameter
    public static final int positionBytesCount = positionElementCount * elementBytes;
    public static final int colorByteCount = colorElementCount * elementBytes;
    public static final int textureByteCount = textureElementCount * elementBytes;
     
    // Byte offsets per parameter
    public static final int positionByteOffset = 0;
    public static final int colorByteOffset = positionByteOffset + positionBytesCount;
    public static final int textureByteOffset = colorByteOffset + colorByteCount;
     
    // The amount of elements that a vertex has
    public static final int elementCount = positionElementCount + 
            colorElementCount + textureElementCount;    
    // The size of a vertex in bytes, like in C/C++: sizeof(Vertex)
    public static final int stride = positionBytesCount + colorByteCount + 
            textureByteCount;
     
    public TexturedVertex() {
		// TODO Auto-generated constructor stub
	}
    
    public TexturedVertex(float x, float y, float z, float r, float g, float b, float u, float v, int i) {
    	this.xyzw = new float[] {x, y, z, 1f};
    	this.rgba = new float[] {r, g, b, 1f};
    	this.uv = new float[] {u, v};
    	this.index = i;
	}
    public TexturedVertex(float x, float y, float z, float r, float g, float b, float a, float u, float v, int i) {
    	this.xyzw = new float[] {x, y, z, 1f};
    	this.rgba = new float[] {r, g, b, a};
    	this.uv = new float[] {u, v};
    	this.index = i;
	}
    
    // Setters
    public void setXYZ(float x, float y, float z) {
        this.setXYZW(x, y, z, 1f);
    }
     
    public void setRGB(float r, float g, float b) {
        this.setRGBA(r, g, b, 1f);
    }
     
    public void setUV(float u, float v) {
        this.uv = new float[] {u, v};
    }
     
    public void setXYZW(float x, float y, float z, float w) {
        this.xyzw = new float[] {x, y, z, w};
    }
     
    public void setRGBA(float r, float g, float b, float a) {
        this.rgba = new float[] {r, g, b, a};
    }
    
    public void setIndex(int index){
    	this.index = index;
    }
     
    // Getters  
    public float[] getElements() {
        float[] out = new float[TexturedVertex.elementCount];
        int i = 0;
         
        // Insert XYZW elements
        out[i++] = this.xyzw[0];
        out[i++] = this.xyzw[1];
        out[i++] = this.xyzw[2];
        out[i++] = this.xyzw[3];
        // Insert RGBA elements
        out[i++] = this.rgba[0];
        out[i++] = this.rgba[1];
        out[i++] = this.rgba[2];
        out[i++] = this.rgba[3];
        // Insert ST elements
        out[i++] = this.uv[0];
        out[i++] = this.uv[1];
         
        return out;
    }
     
    public float[] getXYZW() {
        return new float[] {this.xyzw[0], this.xyzw[1], this.xyzw[2], this.xyzw[3]};
    }
     
    public float[] getRGBA() {
        return new float[] {this.rgba[0], this.rgba[1], this.rgba[2], this.rgba[3]};
    }
     
    public float[] getST() {
        return new float[] {this.uv[0], this.uv[1]};
    }
    
    public int getIndex(){
    	return index;
    }
}