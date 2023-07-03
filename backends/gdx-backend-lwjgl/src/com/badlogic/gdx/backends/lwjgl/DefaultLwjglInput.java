/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.AbstractInput;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntSet;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** An implementation of the {@link LwjglInput} interface hooking a LWJGL panel for input.
 * @author mzechner */
final public class DefaultLwjglInput extends AbstractInput implements LwjglInput {
	static public float keyRepeatInitialTime = 0.4f;
	static public float keyRepeatTime = 0.1f;

	final InputEventQueue eventQueue = new InputEventQueue();
	int deltaX, deltaY;
	final boolean[] justPressedButtons = new boolean[5];
	boolean justTouched;
	final IntSet pressedButtons = new IntSet();
	InputProcessor processor;
	char lastKeyCharPressed;
	float keyRepeatTimer;
	float deltaTime;
	long lastTime;

	public DefaultLwjglInput () {
		Keyboard.enableRepeatEvents(false);
		Mouse.setClipMouseCoordinatesToWindow(false);
	}

	@Override
	public float getAccelerometerX () {
		return 0;
	}

	@Override
	public float getAccelerometerY () {
		return 0;
	}

	@Override
	public float getAccelerometerZ () {
		return 0;
	}

	@Override
	public float getGyroscopeX () {
		return 0;
	}

	@Override
	public float getGyroscopeY () {
		return 0;
	}

	@Override
	public float getGyroscopeZ () {
		return 0;
	}

	@Override
	public void getTextInput (TextInputListener listener, String title, String text, String hint) {
		getTextInput(listener, title, text, hint, OnscreenKeyboardType.Default);
	}

	@Override
	public void getTextInput (final TextInputListener listener, final String title, final String text, final String hint,
		OnscreenKeyboardType type) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run () {
				JPanel panel = new JPanel(new FlowLayout());

				JPanel textPanel = new JPanel() {
					public boolean isOptimizedDrawingEnabled () {
						return false;
					};
				};

				textPanel.setLayout(new OverlayLayout(textPanel));
				panel.add(textPanel);

				final JTextField textField = new JTextField(20);
				textField.setText(text);
				textField.setAlignmentX(0.0f);
				textPanel.add(textField);

				final JLabel placeholderLabel = new JLabel(hint);
				placeholderLabel.setForeground(Color.GRAY);
				placeholderLabel.setAlignmentX(0.0f);
				textPanel.add(placeholderLabel, 0);

				textField.getDocument().addDocumentListener(new DocumentListener() {

					@Override
					public void removeUpdate (DocumentEvent arg0) {
						this.updated();
					}

					@Override
					public void insertUpdate (DocumentEvent arg0) {
						this.updated();
					}

					@Override
					public void changedUpdate (DocumentEvent arg0) {
						this.updated();
					}

					private void updated () {
						if (textField.getText().length() == 0)
							placeholderLabel.setVisible(true);
						else
							placeholderLabel.setVisible(false);
					}
				});

				JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, null,
					null);

				pane.setInitialValue(null);
				pane.setComponentOrientation(JOptionPane.getRootFrame().getComponentOrientation());

				Border border = textField.getBorder();
				placeholderLabel.setBorder(new EmptyBorder(border.getBorderInsets(textField)));

				JDialog dialog = pane.createDialog(null, title);
				pane.selectInitialValue();

				dialog.addWindowFocusListener(new WindowFocusListener() {

					@Override
					public void windowLostFocus (WindowEvent arg0) {
					}

					@Override
					public void windowGainedFocus (WindowEvent arg0) {
						textField.requestFocusInWindow();
					}
				});

				dialog.setModal(true);
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
				dialog.dispose();

				Object selectedValue = pane.getValue();

				if (selectedValue != null && (selectedValue instanceof Integer)
					&& ((Integer)selectedValue).intValue() == JOptionPane.OK_OPTION) {
					listener.input(textField.getText());
				} else {
					listener.canceled();
				}

			}
		});
	}

	@Override
	public int getMaxPointers () {
		return 1;
	}

	@Override
	public int getX () {
		return (int)(Mouse.getX() * Display.getPixelScaleFactor());
	}

	@Override
	public int getY () {
		return Gdx.graphics.getHeight() - 1 - (int)(Mouse.getY() * Display.getPixelScaleFactor());
	}

	@Override
	public boolean isTouched () {
		boolean button = Mouse.isButtonDown(0) || Mouse.isButtonDown(1) || Mouse.isButtonDown(2);
		return button;
	}

	@Override
	public int getX (int pointer) {
		if (pointer > 0) return 0;
		return getX();
	}

	@Override
	public int getY (int pointer) {
		if (pointer > 0) return 0;
		return getY();
	}

	@Override
	public boolean isTouched (int pointer) {
		if (pointer > 0) return false;
		return isTouched();
	}

	@Override
	public float getPressure () {
		return getPressure(0);
	}

	@Override
	public float getPressure (int pointer) {
		return isTouched(pointer) ? 1 : 0;
	}

	@Override
	public void setOnscreenKeyboardVisible (boolean visible) {
	}

	@Override
	public void setOnscreenKeyboardVisible (boolean visible, OnscreenKeyboardType type) {
	}

	@Override
	public void processEvents () {
		eventQueue.drain(processor);
	}

	public int getGdxKeyCode (int lwjglKeyCode) {
		switch (lwjglKeyCode) {
		case Keyboard.KEY_LBRACKET:
			return Keys.LEFT_BRACKET;
		case Keyboard.KEY_RBRACKET:
			return Keys.RIGHT_BRACKET;
		case Keyboard.KEY_GRAVE:
			return Keys.GRAVE;
		case Keyboard.KEY_MULTIPLY:
			return Keys.NUMPAD_MULTIPLY;
		case Keyboard.KEY_PAUSE:
			return Keys.PAUSE;
		case Keyboard.KEY_CAPITAL:
			return Keys.CAPS_LOCK;
		case Keyboard.KEY_SYSRQ:
			return Keys.PRINT_SCREEN;
		case Keyboard.KEY_SCROLL:
			return Keys.SCROLL_LOCK;
		case Keyboard.KEY_NUMLOCK:
			return Keys.NUM_LOCK;
		case Keyboard.KEY_DECIMAL:
			return Keys.NUMPAD_DOT;
		case Keyboard.KEY_DIVIDE:
			return Keys.NUMPAD_DIVIDE;
		case Keyboard.KEY_LMETA:
			return Keys.SYM;
		case Keyboard.KEY_RMETA:
			return Keys.SYM;
		case Keyboard.KEY_NUMPADEQUALS:
			return Keys.NUMPAD_EQUALS;
		case Keyboard.KEY_AT:
			return Keys.AT;
		case Keyboard.KEY_EQUALS:
			return Keys.EQUALS;
		case Keyboard.KEY_NUMPADCOMMA:
			return Keys.NUMPAD_COMMA;
		case Keyboard.KEY_NUMPADENTER:
			return Keys.NUMPAD_ENTER;
		case Keyboard.KEY_0:
			return Keys.NUM_0;
		case Keyboard.KEY_1:
			return Keys.NUM_1;
		case Keyboard.KEY_2:
			return Keys.NUM_2;
		case Keyboard.KEY_3:
			return Keys.NUM_3;
		case Keyboard.KEY_4:
			return Keys.NUM_4;
		case Keyboard.KEY_5:
			return Keys.NUM_5;
		case Keyboard.KEY_6:
			return Keys.NUM_6;
		case Keyboard.KEY_7:
			return Keys.NUM_7;
		case Keyboard.KEY_8:
			return Keys.NUM_8;
		case Keyboard.KEY_9:
			return Keys.NUM_9;
		case Keyboard.KEY_A:
			return Keys.A;
		case Keyboard.KEY_B:
			return Keys.B;
		case Keyboard.KEY_C:
			return Keys.C;
		case Keyboard.KEY_D:
			return Keys.D;
		case Keyboard.KEY_E:
			return Keys.E;
		case Keyboard.KEY_F:
			return Keys.F;
		case Keyboard.KEY_G:
			return Keys.G;
		case Keyboard.KEY_H:
			return Keys.H;
		case Keyboard.KEY_I:
			return Keys.I;
		case Keyboard.KEY_J:
			return Keys.J;
		case Keyboard.KEY_K:
			return Keys.K;
		case Keyboard.KEY_L:
			return Keys.L;
		case Keyboard.KEY_M:
			return Keys.M;
		case Keyboard.KEY_N:
			return Keys.N;
		case Keyboard.KEY_O:
			return Keys.O;
		case Keyboard.KEY_P:
			return Keys.P;
		case Keyboard.KEY_Q:
			return Keys.Q;
		case Keyboard.KEY_R:
			return Keys.R;
		case Keyboard.KEY_S:
			return Keys.S;
		case Keyboard.KEY_T:
			return Keys.T;
		case Keyboard.KEY_U:
			return Keys.U;
		case Keyboard.KEY_V:
			return Keys.V;
		case Keyboard.KEY_W:
			return Keys.W;
		case Keyboard.KEY_X:
			return Keys.X;
		case Keyboard.KEY_Y:
			return Keys.Y;
		case Keyboard.KEY_Z:
			return Keys.Z;
		case Keyboard.KEY_LMENU:
			return Keys.ALT_LEFT;
		case Keyboard.KEY_RMENU:
			return Keys.ALT_RIGHT;
		case Keyboard.KEY_BACKSLASH:
			return Keys.BACKSLASH;
		case Keyboard.KEY_COMMA:
			return Keys.COMMA;
		case Keyboard.KEY_DELETE:
			return Keys.FORWARD_DEL;
		case Keyboard.KEY_LEFT:
			return Keys.DPAD_LEFT;
		case Keyboard.KEY_RIGHT:
			return Keys.DPAD_RIGHT;
		case Keyboard.KEY_UP:
			return Keys.DPAD_UP;
		case Keyboard.KEY_DOWN:
			return Keys.DPAD_DOWN;
		case Keyboard.KEY_RETURN:
			return Keys.ENTER;
		case Keyboard.KEY_HOME:
			return Keys.HOME;
		case Keyboard.KEY_MINUS:
			return Keys.MINUS;
		case Keyboard.KEY_PERIOD:
			return Keys.PERIOD;
		case Keyboard.KEY_ADD:
			return Keys.NUMPAD_ADD;
		case Keyboard.KEY_SEMICOLON:
			return Keys.SEMICOLON;
		case Keyboard.KEY_LSHIFT:
			return Keys.SHIFT_LEFT;
		case Keyboard.KEY_RSHIFT:
			return Keys.SHIFT_RIGHT;
		case Keyboard.KEY_SLASH:
			return Keys.SLASH;
		case Keyboard.KEY_SPACE:
			return Keys.SPACE;
		case Keyboard.KEY_TAB:
			return Keys.TAB;
		case Keyboard.KEY_LCONTROL:
			return Keys.CONTROL_LEFT;
		case Keyboard.KEY_RCONTROL:
			return Keys.CONTROL_RIGHT;
		case Keyboard.KEY_NEXT:
			return Keys.PAGE_DOWN;
		case Keyboard.KEY_PRIOR:
			return Keys.PAGE_UP;
		case Keyboard.KEY_ESCAPE:
			return Keys.ESCAPE;
		case Keyboard.KEY_END:
			return Keys.END;
		case Keyboard.KEY_INSERT:
			return Keys.INSERT;
		case Keyboard.KEY_BACK:
			return Keys.DEL;
		case Keyboard.KEY_SUBTRACT:
			return Keys.NUMPAD_SUBTRACT;
		case Keyboard.KEY_APOSTROPHE:
			return Keys.APOSTROPHE;
		case Keyboard.KEY_F1:
			return Keys.F1;
		case Keyboard.KEY_F2:
			return Keys.F2;
		case Keyboard.KEY_F3:
			return Keys.F3;
		case Keyboard.KEY_F4:
			return Keys.F4;
		case Keyboard.KEY_F5:
			return Keys.F5;
		case Keyboard.KEY_F6:
			return Keys.F6;
		case Keyboard.KEY_F7:
			return Keys.F7;
		case Keyboard.KEY_F8:
			return Keys.F8;
		case Keyboard.KEY_F9:
			return Keys.F9;
		case Keyboard.KEY_F10:
			return Keys.F10;
		case Keyboard.KEY_F11:
			return Keys.F11;
		case Keyboard.KEY_F12:
			return Keys.F12;
		case Keyboard.KEY_F13:
			return Keys.F13;
		case Keyboard.KEY_F14:
			return Keys.F14;
		case Keyboard.KEY_F15:
			return Keys.F15;
		case Keyboard.KEY_F16:
			return Keys.F16;
		case Keyboard.KEY_F17:
			return Keys.F17;
		case Keyboard.KEY_F18:
			return Keys.F18;
		case Keyboard.KEY_COLON:
			return Keys.COLON;
		case Keyboard.KEY_NUMPAD0:
			return Keys.NUMPAD_0;
		case Keyboard.KEY_NUMPAD1:
			return Keys.NUMPAD_1;
		case Keyboard.KEY_NUMPAD2:
			return Keys.NUMPAD_2;
		case Keyboard.KEY_NUMPAD3:
			return Keys.NUMPAD_3;
		case Keyboard.KEY_NUMPAD4:
			return Keys.NUMPAD_4;
		case Keyboard.KEY_NUMPAD5:
			return Keys.NUMPAD_5;
		case Keyboard.KEY_NUMPAD6:
			return Keys.NUMPAD_6;
		case Keyboard.KEY_NUMPAD7:
			return Keys.NUMPAD_7;
		case Keyboard.KEY_NUMPAD8:
			return Keys.NUMPAD_8;
		case Keyboard.KEY_NUMPAD9:
			return Keys.NUMPAD_9;
		default:
			return Keys.UNKNOWN;
		}
	}

	@Override
	public void update () {
		updateTime();
		updateMouse();
		updateKeyboard();
	}

	private int toGdxButton (int button) {
		if (button == 0) return Buttons.LEFT;
		if (button == 1) return Buttons.RIGHT;
		if (button == 2) return Buttons.MIDDLE;
		if (button == 3) return Buttons.BACK;
		if (button == 4) return Buttons.FORWARD;
		return -1;
	}

	void updateTime () {
		long thisTime = System.nanoTime();
		deltaTime = (thisTime - lastTime) / 1000000000.0f;
		lastTime = thisTime;
	}

	void updateMouse () {
		if (justTouched) {
			justTouched = false;
			for (int i = 0; i < justPressedButtons.length; i++) {
				justPressedButtons[i] = false;
			}
		}
		if (Mouse.isCreated()) {
			int events = 0;
			while (Mouse.next()) {
				events++;
				int x = (int)(Mouse.getEventX() * Display.getPixelScaleFactor());
				int y = Gdx.graphics.getHeight() - (int)(Mouse.getEventY() * Display.getPixelScaleFactor()) - 1;
				int button = Mouse.getEventButton();
				int gdxButton = toGdxButton(button);
				if (button != -1 && gdxButton == -1) continue; // Ignore unknown button.
				long time = Mouse.getEventNanoseconds();

				// could be drag, scroll or move
				if (button == -1) {
					if (Mouse.getEventDWheel() != 0)
						eventQueue.scrolled(0, (int)-Math.signum(Mouse.getEventDWheel()), time);
					else if (pressedButtons.size > 0)
						eventQueue.touchDragged(x, y, 0, time);
					else
						eventQueue.mouseMoved(x, y, time);
					// nope, it's a down or up event.
				} else if (Mouse.getEventButtonState()) {
					eventQueue.touchDown(x, y, 0, gdxButton, time);
					pressedButtons.add(gdxButton);
					justPressedButtons[gdxButton] = true;
					justTouched = true;
				} else {
					eventQueue.touchUp(x, y, 0, gdxButton, time);
					pressedButtons.remove(gdxButton);
				}
				deltaX = (int)(Mouse.getEventDX() * Display.getPixelScaleFactor());
				deltaY = (int)(Mouse.getEventDY() * Display.getPixelScaleFactor());
			}

			if (events == 0) {
				deltaX = 0;
				deltaY = 0;
			} else {
				Gdx.graphics.requestRendering();
			}
		}
	}

	void updateKeyboard () {
		if (keyJustPressed) {
			keyJustPressed = false;
			for (int i = 0; i < justPressedKeys.length; i++) {
				justPressedKeys[i] = false;
			}
		}
		if (lastKeyCharPressed != 0) {
			keyRepeatTimer -= deltaTime;
			if (keyRepeatTimer < 0) {
				keyRepeatTimer = DefaultLwjglInput.keyRepeatTime;
				eventQueue.keyTyped(lastKeyCharPressed, System.nanoTime());
				Gdx.graphics.requestRendering();
			}
		}

		if (Keyboard.isCreated()) {
			while (Keyboard.next()) {
				int keyCode = getGdxKeyCode(Keyboard.getEventKey());
				char keyChar = Keyboard.getEventCharacter();
				long time = Keyboard.getEventNanoseconds();
				if (Keyboard.getEventKeyState() || (keyCode == 0 && keyChar != 0 && Character.isDefined(keyChar))) {
					switch (keyCode) {
					case Keys.DEL:
						keyChar = 8;
						break;
					case Keys.FORWARD_DEL:
						keyChar = 127;
						break;
					}

					if (keyCode != 0) {
						eventQueue.keyDown(keyCode, time);
						pressedKeyCount++;
						keyJustPressed = true;
						pressedKeys[keyCode] = true;
						justPressedKeys[keyCode] = true;
						lastKeyCharPressed = keyChar;
						keyRepeatTimer = DefaultLwjglInput.keyRepeatInitialTime;
					}

					eventQueue.keyTyped(keyChar, time);
				} else {
					eventQueue.keyUp(keyCode, time);
					pressedKeyCount--;
					pressedKeys[keyCode] = false;
					lastKeyCharPressed = 0;
				}
				Gdx.graphics.requestRendering();
			}
		}
	}

	@Override
	public void setInputProcessor (InputProcessor processor) {
		this.processor = processor;
	}

	@Override
	public InputProcessor getInputProcessor () {
		return this.processor;
	}

	@Override
	public void vibrate (int milliseconds) {
	}

	@Override
	public void vibrate (int milliseconds, boolean fallback) {
	}

	@Override
	public void vibrate (int milliseconds, int amplitude, boolean fallback) {
	}

	@Override
	public void vibrate (VibrationType vibrationType) {
	}

	@Override
	public boolean justTouched () {
		return justTouched;
	}

	private int toLwjglButton (int button) {
		switch (button) {
		case Buttons.LEFT:
			return 0;
		case Buttons.RIGHT:
			return 1;
		case Buttons.MIDDLE:
			return 2;
		case Buttons.BACK:
			return 3;
		case Buttons.FORWARD:
			return 4;
		}
		return 0;
	}

	@Override
	public boolean isButtonPressed (int button) {
		return Mouse.isButtonDown(toLwjglButton(button));
	}

	@Override
	public boolean isButtonJustPressed (int button) {
		if (button < 0 || button >= justPressedButtons.length) return false;
		return justPressedButtons[button];
	}

	@Override
	public float getAzimuth () {
		return 0;
	}

	@Override
	public float getPitch () {
		return 0;
	}

	@Override
	public float getRoll () {
		return 0;
	}

	@Override
	public boolean isPeripheralAvailable (Peripheral peripheral) {
		return peripheral == Peripheral.HardwareKeyboard;
	}

	@Override
	public int getRotation () {
		return 0;
	}

	@Override
	public Orientation getNativeOrientation () {
		return Orientation.Landscape;
	}

	@Override
	public void setCursorCatched (boolean catched) {
		Mouse.setGrabbed(catched);
	}

	@Override
	public boolean isCursorCatched () {
		return Mouse.isGrabbed();
	}

	@Override
	public int getDeltaX () {
		return deltaX;
	}

	@Override
	public int getDeltaX (int pointer) {
		if (pointer == 0) return deltaX;
		return 0;
	}

	@Override
	public int getDeltaY () {
		return -deltaY;
	}

	@Override
	public int getDeltaY (int pointer) {
		if (pointer == 0) return -deltaY;
		return 0;
	}

	@Override
	public void setCursorPosition (int x, int y) {
		Mouse.setCursorPosition(x, Gdx.graphics.getHeight() - 1 - y);
	}

	@Override
	public long getCurrentEventTime () {
		return eventQueue.getCurrentEventTime();
	}

	@Override
	public void getRotationMatrix (float[] matrix) {
	}
}
