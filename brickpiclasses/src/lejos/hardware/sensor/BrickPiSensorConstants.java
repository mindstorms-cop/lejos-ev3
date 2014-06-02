package lejos.hardware.sensor;

public interface BrickPiSensorConstants {
    public static final int PORTS = 5;
    public static final int MOTORS = 4;
    
    public static final byte CMD_NONE = (byte)'-';
    public static final byte CMD_FLOAT = (byte)'f';
    public static final byte CMD_SET = (byte)'0';
    public static final byte CMD_COL_COL = 0xd;
    public static final byte CMD_COL_RED = 0xe;
    public static final byte CMD_COL_GRN = 0xf;
    public static final byte CMD_COL_BLU = 0x11;
    public static final byte CMD_COL_AMB = 0x12;
    public static final byte CMD_PIN1 = 0x1;
    public static final byte CMD_PIN5 = 0x2;
}
