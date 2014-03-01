package lejos.hardware;

/**
 * Abstraction for receiver of button events.
 * @see lejos.hardware.Button#addButtonListener
 */
public interface ButtonListener
{
  public void buttonPressed (Key b);
  public void buttonReleased (Key b);
}
