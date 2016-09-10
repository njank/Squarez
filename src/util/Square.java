package util;

public class Square {
    public int x;
    public int y;
    public int size;
    public Type type;
    public int speed;
    public int direction;
    
    public static enum Type {
      BLUE, GREEN, BLACK, ORANGE, MAGENTA, RED;
    }

    public Square(int x, int y, Type type, int size, int direction) {
        this.x = x;
        this.y = y;
        switch (this.type = type) {
            case BLUE:
                this.speed = 3;
                this.size = size;
                break;
            case GREEN:
                this.speed = 2;
                this.size = ((int) (size / 0.8f));
                break;
            case BLACK:
                this.speed = 4;
                this.size = ((int) (size / 1.2f));
                break;
            case ORANGE:
                this.speed = 5;
                this.size = ((int) (size / 1.5f));
                break;
            case MAGENTA:
                this.speed = 6;
                this.size = ((int) (size / 1.7f));
                break;
            case RED:
                this.size = size;
                break;
            default:
                this.speed = 3;
        }
        this.direction = direction;
    }
    
    // move the Square by the amount of level+speed in the current direction
    // returns false if the Square is outside of the visible display
    public boolean move(int level, float factor, int width, int height) {
        switch (this.direction) {
            case 1: return (this.y += ((level + this.speed) * factor)) < height + this.size - 1;
            case 2: return (this.x -= ((level + this.speed) * factor)) + this.size + 1 > 0;
            case 3: return (this.y -= ((level + this.speed) * factor)) + this.size + 1 > 0;
            case 4: return (this.x += ((level + this.speed) * factor)) < width + this.size - 1;
        }
        return false;
    }

    public void grow(int amount) {
        this.size += amount;
    }
    
    public boolean intersects(Square s) {
        int tw = this.size;
        int th = this.size;
        int rw = s.size;
        int rh = s.size;
        if ((rw <= 0) || (rh <= 0) || (tw <= 0) || (th <= 0)) {
            return false;
        }
        int tx = this.x;
        int ty = this.y;
        int rx = s.x;
        int ry = s.y;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;

        return ((rw < rx) || (rw > tx)) && ((rh < ry) || (rh > ty)) && ((tw < tx) || (tw > rx)) && ((th < ty) || (th > ry));
    }

    public void changeDirection(int rotation) {
        this.direction = (this.direction % 4 + rotation);
    }
}
