package tw.freddie.ledcolor.commands;

public class LEDColorCommand extends Command {
    private int mColor;
    public LEDColorCommand(int color) {
        super();
        mColor = color;
    }

    @Override
    public byte[] getRawData() {
        byte[] command = new byte[4];
        command[0] = (byte)((mColor >> 16) & 0x000000FF);
        command[1] = (byte)(((mColor >> 8) & 0x000000FF));
        command[2] = (byte)(mColor & 0x000000FF);
        command[3] = 0;

        return command;
    }
}
