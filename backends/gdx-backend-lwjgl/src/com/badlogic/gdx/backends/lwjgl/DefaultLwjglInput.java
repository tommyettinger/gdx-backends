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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

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

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntSet;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Pool;

/** An implementation of the {@link LwjglInput} interface hooking a LWJGL panel for input.
 * 
 * @author mzechner */
final public class DefaultLwjglInput implements LwjglInput {
	static public float keyRepeatInitialTime = 0.4f;
	static public float keyRepeatTime = 0.1f;
	
	final InputEventQueue eventQueue = new InputEventQueue();
	int deltaX, deltaY;
	int pressedKeys = 0;
	boolean keyJustPressed = false;
	boolean[] justPressedKeys = new boolean[Keys.MAX_KEYCODE + 1];
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

	public float getGyroscopeY () {
		return 0;
	}

	public float getGyroscopeZ () {
		return 0;
	}

	@Override
	public void getTextInput(TextInputListener listener, String title, String text, String hint) {
		getTextInput(listener, title, text, hint, OnscreenKeyboardType.Default);
	}

	@Override
	public void getTextInput (final TextInputListener listener, final String title, final String text, final String hint, OnscreenKeyboardType type) {
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

	public boolean isAccelerometerAvailable () {
		return false;
	}
	
	public boolean isGyroscopeAvailable () {
		return false;
	}

	public boolean isKeyPressed (int key) {
		if (!Keyboard.isCreated()) return false;

		if (key == Input.Keys.ANY_KEY)
			return pressedKeys > 0;
		else
			return Keyboard.isKeyDown(getLwjglKeyCode(key));
	}

	@Override
	public boolean isKeyJustPressed (int key) {
		if (key == Input.Keys.ANY_KEY) {
			return keyJustPressed;
		}
		if (key < 0 || key >= justPressedKeys.length) {
			return false;
		}
		return justPressedKeys[key];
	}
	
	@Override
	public boolean isTouched () {
		boolean button = Mouse.isButtonDown(0) || Mouse.isButtonDown(1) || Mouse.isButtonDown(2);
		return button;
	}

	public int getX (int pointer) {
		if (pointer > 0)
			return 0;
		else
			return getX();
	}

	public int getY (int pointer) {
		if (pointer > 0)
			return 0;
		else
			return getY();
	}

	public boolean isTouched (int pointer) {
		if (pointer > 0)
			return false;
		else
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

	public boolean supportsMultitouch () {
		return false;
	}

	@Override
	public void setOnscreenKeyboardVisible (boolean visible) {

	}

	@Override
	public void setOnscreenKeyboardVisible(boolean visible, OnscreenKeyboardType type) {

	}

	@Override
	public void setCatchBackKey (boolean catchBack) {

	}

	@Override
	public boolean isCatchBackKey () {
		return false;
	}	

	@Override
	public void setCatchMenuKey (boolean catchMenu) {
		
	}
	
	@Override
	public boolean isCatchMenuKey () {
		return false;
	}

	@Override
	public void setCatchKey (int keycode, boolean catchKey) {

	}

	@Override
	public boolean isCatchKey (int keycode) {
		return false;
	}

	@Override
	public void processEvents () {
		eventQueue.drain(processor);
	}

	public static int getGdxKeyCode (int lwjglKeyCode) {
		switch (lwjglKeyCode) {
		case Keyboard.KEY_LBRACKET:
			return Input.Keys.LEFT_BRACKET;
		case Keyboard.KEY_RBRACKET:
			return Input.Keys.RIGHT_BRACKET;
		case Keyboard.KEY_GRAVE:
			return Input.Keys.GRAVE;
		case Keyboard.KEY_MULTIPLY:
			return Input.Keys.STAR;
		case Keyboard.KEY_NUMLOCK:
			return Input.Keys.NUM;
		case Keyboard.KEY_DECIMAL:
			return Input.Keys.PERIOD;
		case Keyboard.KEY_DIVIDE:
			return Input.Keys.SLASH;
		case Keyboard.KEY_LMETA:
			return Input.Keys.SYM;
		case Keyboard.KEY_RMETA:
			return Input.Keys.SYM;
		case Keyboard.KEY_NUMPADEQUALS:
			return Input.Keys.EQUALS;
		case Keyboard.KEY_AT:
			return Input.Keys.AT;
		case Keyboard.KEY_EQUALS:
			return Input.Keys.EQUALS;
		case Keyboard.KEY_NUMPADCOMMA:
			return Input.Keys.COMMA;
		case Keyboard.KEY_NUMPADENTER:
			return Input.Keys.ENTER;
		case Keyboard.KEY_0:
			return Input.Keys.NUM_0;
		case Keyboard.KEY_1:
			return Input.Keys.NUM_1;
		case Keyboard.KEY_2:
			return Input.Keys.NUM_2;
		case Keyboard.KEY_3:
			return Input.Keys.NUM_3;
		case Keyboard.KEY_4:
			return Input.Keys.NUM_4;
		case Keyboard.KEY_5:
			return Input.Keys.NUM_5;
		case Keyboard.KEY_6:
			return Input.Keys.NUM_6;
		case Keyboard.KEY_7:
			return Input.Keys.NUM_7;
		case Keyboard.KEY_8:
			return Input.Keys.NUM_8;
		case Keyboard.KEY_9:
			return Input.Keys.NUM_9;
		case Keyboard.KEY_A:
			return Input.Keys.A;
		case Keyboard.KEY_B:
			return Input.Keys.B;
		case Keyboard.KEY_C:
			return Input.Keys.C;
		case Keyboard.KEY_D:
			return Input.Keys.D;
		case Keyboard.KEY_E:
			return Input.Keys.E;
		case Keyboard.KEY_F:
			return Input.Keys.F;
		case Keyboard.KEY_G:
			return Input.Keys.G;
		case Keyboard.KEY_H:
			return Input.Keys.H;
		case Keyboard.KEY_I:
			return Input.Keys.I;
		case Keyboard.KEY_J:
			return Input.Keys.J;
		case Keyboard.KEY_K:
			return Input.Keys.K;
		case Keyboard.KEY_L:
			return Input.Keys.L;
		case Keyboard.KEY_M:
			return Input.Keys.M;
		case Keyboard.KEY_N:
			return Input.Keys.N;
		case Keyboard.KEY_O:
			return Input.Keys.O;
		case Keyboard.KEY_P:
			return Input.Keys.P;
		case Keyboard.KEY_Q:
			return Input.Keys.Q;
		case Keyboard.KEY_R:
			return Input.Keys.R;
		case Keyboard.KEY_S:
			return Input.Keys.S;
		case Keyboard.KEY_T:
			return Input.Keys.T;
		case Keyboard.KEY_U:
			return Input.Keys.U;
		case Keyboard.KEY_V:
			return Input.Keys.V;
		case Keyboard.KEY_W:
			return Input.Keys.W;
		case Keyboard.KEY_X:
			return Input.Keys.X;
		case Keyboard.KEY_Y:
			return Input.Keys.Y;
		case Keyboard.KEY_Z:
			return Input.Keys.Z;
		case Keyboard.KEY_LMENU:
			return Input.Keys.ALT_LEFT;
		case Keyboard.KEY_RMENU:
			return Input.Keys.ALT_RIGHT;
		case Keyboard.KEY_BACKSLASH:
			return Input.Keys.BACKSLASH;
		case Keyboard.KEY_COMMA:
			return Input.Keys.COMMA;
		case Keyboard.KEY_DELETE:
			return Input.Keys.FORWARD_DEL;
		case Keyboard.KEY_LEFT:
			return Input.Keys.DPAD_LEFT;
		case Keyboard.KEY_RIGHT:
			return Input.Keys.DPAD_RIGHT;
		case Keyboard.KEY_UP:
			return Input.Keys.DPAD_UP;
		case Keyboard.KEY_DOWN:
			return Input.Keys.DPAD_DOWN;
		case Keyboard.KEY_RETURN:
			return Input.Keys.ENTER;
		case Keyboard.KEY_HOME:
			return Input.Keys.HOME;
		case Keyboard.KEY_MINUS:
			return Input.Keys.MINUS;
		case Keyboard.KEY_PERIOD:
			return Input.Keys.PERIOD;
		case Keyboard.KEY_ADD:
			return Input.Keys.PLUS;
		case Keyboard.KEY_SEMICOLON:
			return Input.Keys.SEMICOLON;
		case Keyboard.KEY_LSHIFT:
			return Input.Keys.SHIFT_LEFT;
		case Keyboard.KEY_RSHIFT:
			return Input.Keys.SHIFT_RIGHT;
		case Keyboard.KEY_SLASH:
			return Input.Keys.SLASH;
		case Keyboard.KEY_SPACE:
			return Input.Keys.SPACE;
		case Keyboard.KEY_TAB:
			return Input.Keys.TAB;
		case Keyboard.KEY_LCONTROL:
			return Input.Keys.CONTROL_LEFT;
		case Keyboard.KEY_RCONTROL:
			return Input.Keys.CONTROL_RIGHT;
		case Keyboard.KEY_NEXT:
			return Input.Keys.PAGE_DOWN;
		case Keyboard.KEY_PRIOR:
			return Input.Keys.PAGE_UP;
		case Keyboard.KEY_ESCAPE:
			return Input.Keys.ESCAPE;
		case Keyboard.KEY_END:
			return Input.Keys.END;
		case Keyboard.KEY_INSERT:
			return Input.Keys.INSERT;
		case Keyboard.KEY_BACK:
			return Input.Keys.DEL;
		case Keyboard.KEY_SUBTRACT:
			return Input.Keys.MINUS;
		case Keyboard.KEY_APOSTROPHE:
			return Input.Keys.APOSTROPHE;
		case Keyboard.KEY_F1:
			return Input.Keys.F1;
		case Keyboard.KEY_F2:
			return Input.Keys.F2;
		case Keyboard.KEY_F3:
			return Input.Keys.F3;
		case Keyboard.KEY_F4:
			return Input.Keys.F4;
		case Keyboard.KEY_F5:
			return Input.Keys.F5;
		case Keyboard.KEY_F6:
			return Input.Keys.F6;
		case Keyboard.KEY_F7:
			return Input.Keys.F7;
		case Keyboard.KEY_F8:
			return Input.Keys.F8;
		case Keyboard.KEY_F9:
			return Input.Keys.F9;
		case Keyboard.KEY_F10:
			return Input.Keys.F10;
		case Keyboard.KEY_F11:
			return Input.Keys.F11;
		case Keyboard.KEY_F12:
			return Input.Keys.F12;
		case Keyboard.KEY_COLON:
			return Input.Keys.COLON;
		case Keyboard.KEY_NUMPAD0:
			return Input.Keys.NUMPAD_0;
		case Keyboard.KEY_NUMPAD1:
			return Input.Keys.NUMPAD_1;
		case Keyboard.KEY_NUMPAD2:
			return Input.Keys.NUMPAD_2;
		case Keyboard.KEY_NUMPAD3:
			return Input.Keys.NUMPAD_3;
		case Keyboard.KEY_NUMPAD4:
			return Input.Keys.NUMPAD_4;
		case Keyboard.KEY_NUMPAD5:
			return Input.Keys.NUMPAD_5;
		case Keyboard.KEY_NUMPAD6:
			return Input.Keys.NUMPAD_6;
		case Keyboard.KEY_NUMPAD7:
			return Input.Keys.NUMPAD_7;
		case Keyboard.KEY_NUMPAD8:
			return Input.Keys.NUMPAD_8;
		case Keyboard.KEY_NUMPAD9:
			return Input.Keys.NUMPAD_9;
		default:
			return Input.Keys.UNKNOWN;
		}
	}

	public static int getLwjglKeyCode (int gdxKeyCode) {
		switch (gdxKeyCode) {
		case Input.Keys.APOSTROPHE:
			return Keyboard.KEY_APOSTROPHE;
		case Input.Keys.LEFT_BRACKET:
			return Keyboard.KEY_LBRACKET;
		case Input.Keys.RIGHT_BRACKET:
			return Keyboard.KEY_RBRACKET;
		case Input.Keys.GRAVE:
			return Keyboard.KEY_GRAVE;
		case Input.Keys.STAR:
			return Keyboard.KEY_MULTIPLY;
		case Input.Keys.NUM:
			return Keyboard.KEY_NUMLOCK;
		case Input.Keys.AT:
			return Keyboard.KEY_AT;
		case Input.Keys.EQUALS:
			return Keyboard.KEY_EQUALS;
		case Input.Keys.SYM:
			return Keyboard.KEY_LMETA;
		case Input.Keys.NUM_0:
			return Keyboard.KEY_0;
		case Input.Keys.NUM_1:
			return Keyboard.KEY_1;
		case Input.Keys.NUM_2:
			return Keyboard.KEY_2;
		case Input.Keys.NUM_3:
			return Keyboard.KEY_3;
		case Input.Keys.NUM_4:
			return Keyboard.KEY_4;
		case Input.Keys.NUM_5:
			return Keyboard.KEY_5;
		case Input.Keys.NUM_6:
			return Keyboard.KEY_6;
		case Input.Keys.NUM_7:
			return Keyboard.KEY_7;
		case Input.Keys.NUM_8:
			return Keyboard.KEY_8;
		case Input.Keys.NUM_9:
			return Keyboard.KEY_9;
		case Input.Keys.A:
			return Keyboard.KEY_A;
		case Input.Keys.B:
			return Keyboard.KEY_B;
		case Input.Keys.C:
			return Keyboard.KEY_C;
		case Input.Keys.D:
			return Keyboard.KEY_D;
		case Input.Keys.E:
			return Keyboard.KEY_E;
		case Input.Keys.F:
			return Keyboard.KEY_F;
		case Input.Keys.G:
			return Keyboard.KEY_G;
		case Input.Keys.H:
			return Keyboard.KEY_H;
		case Input.Keys.I:
			return Keyboard.KEY_I;
		case Input.Keys.J:
			return Keyboard.KEY_J;
		case Input.Keys.K:
			return Keyboard.KEY_K;
		case Input.Keys.L:
			return Keyboard.KEY_L;
		case Input.Keys.M:
			return Keyboard.KEY_M;
		case Input.Keys.N:
			return Keyboard.KEY_N;
		case Input.Keys.O:
			return Keyboard.KEY_O;
		case Input.Keys.P:
			return Keyboard.KEY_P;
		case Input.Keys.Q:
			return Keyboard.KEY_Q;
		case Input.Keys.R:
			return Keyboard.KEY_R;
		case Input.Keys.S:
			return Keyboard.KEY_S;
		case Input.Keys.T:
			return Keyboard.KEY_T;
		case Input.Keys.U:
			return Keyboard.KEY_U;
		case Input.Keys.V:
			return Keyboard.KEY_V;
		case Input.Keys.W:
			return Keyboard.KEY_W;
		case Input.Keys.X:
			return Keyboard.KEY_X;
		case Input.Keys.Y:
			return Keyboard.KEY_Y;
		case Input.Keys.Z:
			return Keyboard.KEY_Z;
		case Input.Keys.ALT_LEFT:
			return Keyboard.KEY_LMENU;
		case Input.Keys.ALT_RIGHT:
			return Keyboard.KEY_RMENU;
		case Input.Keys.BACKSLASH:
			return Keyboard.KEY_BACKSLASH;
		case Input.Keys.COMMA:
			return Keyboard.KEY_COMMA;
		case Input.Keys.FORWARD_DEL:
			return Keyboard.KEY_DELETE;
		case Input.Keys.DPAD_LEFT:
			return Keyboard.KEY_LEFT;
		case Input.Keys.DPAD_RIGHT:
			return Keyboard.KEY_RIGHT;
		case Input.Keys.DPAD_UP:
			return Keyboard.KEY_UP;
		case Input.Keys.DPAD_DOWN:
			return Keyboard.KEY_DOWN;
		case Input.Keys.ENTER:
			return Keyboard.KEY_RETURN;
		case Input.Keys.HOME:
			return Keyboard.KEY_HOME;
		case Input.Keys.END:
			return Keyboard.KEY_END;
		case Input.Keys.PAGE_DOWN:
			return Keyboard.KEY_NEXT;
		case Input.Keys.PAGE_UP:
			return Keyboard.KEY_PRIOR;
		case Input.Keys.INSERT:
			return Keyboard.KEY_INSERT;
		case Input.Keys.MINUS:
			return Keyboard.KEY_MINUS;
		case Input.Keys.PERIOD:
			return Keyboard.KEY_PERIOD;
		case Input.Keys.PLUS:
			return Keyboard.KEY_ADD;
		case Input.Keys.SEMICOLON:
			return Keyboard.KEY_SEMICOLON;
		case Input.Keys.SHIFT_LEFT:
			return Keyboard.KEY_LSHIFT;
		case Input.Keys.SHIFT_RIGHT:
			return Keyboard.KEY_RSHIFT;
		case Input.Keys.SLASH:
			return Keyboard.KEY_SLASH;
		case Input.Keys.SPACE:
			return Keyboard.KEY_SPACE;
		case Input.Keys.TAB:
			return Keyboard.KEY_TAB;
		case Input.Keys.DEL:
			return Keyboard.KEY_BACK;
		case Input.Keys.CONTROL_LEFT:
			return Keyboard.KEY_LCONTROL;
		case Input.Keys.CONTROL_RIGHT:
			return Keyboard.KEY_RCONTROL;
		case Input.Keys.ESCAPE:
			return Keyboard.KEY_ESCAPE;
		case Input.Keys.F1:
			return Keyboard.KEY_F1;
		case Input.Keys.F2:
			return Keyboard.KEY_F2;
		case Input.Keys.F3:
			return Keyboard.KEY_F3;
		case Input.Keys.F4:
			return Keyboard.KEY_F4;
		case Input.Keys.F5:
			return Keyboard.KEY_F5;
		case Input.Keys.F6:
			return Keyboard.KEY_F6;
		case Input.Keys.F7:
			return Keyboard.KEY_F7;
		case Input.Keys.F8:
			return Keyboard.KEY_F8;
		case Input.Keys.F9:
			return Keyboard.KEY_F9;
		case Input.Keys.F10:
			return Keyboard.KEY_F10;
		case Input.Keys.F11:
			return Keyboard.KEY_F11;
		case Input.Keys.F12:
			return Keyboard.KEY_F12;
		case Input.Keys.COLON:
			return Keyboard.KEY_COLON;
		case Input.Keys.NUMPAD_0:
			return Keyboard.KEY_NUMPAD0;
		case Input.Keys.NUMPAD_1:
			return Keyboard.KEY_NUMPAD1;
		case Input.Keys.NUMPAD_2:
			return Keyboard.KEY_NUMPAD2;
		case Input.Keys.NUMPAD_3:
			return Keyboard.KEY_NUMPAD3;
		case Input.Keys.NUMPAD_4:
			return Keyboard.KEY_NUMPAD4;
		case Input.Keys.NUMPAD_5:
			return Keyboard.KEY_NUMPAD5;
		case Input.Keys.NUMPAD_6:
			return Keyboard.KEY_NUMPAD6;
		case Input.Keys.NUMPAD_7:
			return Keyboard.KEY_NUMPAD7;
		case Input.Keys.NUMPAD_8:
			return Keyboard.KEY_NUMPAD8;
		case Input.Keys.NUMPAD_9:
			return Keyboard.KEY_NUMPAD9;
		default:
			return Keyboard.KEY_NONE;
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
						pressedKeys++;
						keyJustPressed = true;
						justPressedKeys[keyCode] = true;
						lastKeyCharPressed = keyChar;
						keyRepeatTimer = DefaultLwjglInput.keyRepeatInitialTime;
					}
					
					eventQueue.keyTyped(keyChar, time);
				} else {
					eventQueue.keyUp(keyCode, time);
					pressedKeys--;
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
	public boolean isButtonJustPressed(int button) {
		if(button < 0 || button >= justPressedButtons.length) return false;
		return justPressedButtons[button];
	}

	@Override
	public void vibrate (long[] pattern, int repeat) {
	}

	@Override
	public void cancelVibrate () {
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
		if (pointer == 0)
			return deltaX;
		else
			return 0;
	}

	@Override
	public int getDeltaY () {
		return -deltaY;
	}

	@Override
	public int getDeltaY (int pointer) {
		if (pointer == 0)
			return -deltaY;
		else
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
