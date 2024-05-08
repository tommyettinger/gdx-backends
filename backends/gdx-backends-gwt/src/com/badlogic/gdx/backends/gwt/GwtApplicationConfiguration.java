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

package com.badlogic.gdx.backends.gwt;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.gwt.GwtGraphics.OrientationLockType;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;

public class GwtApplicationConfiguration {
	/** If true, audio backend will not be used. This means {@link Application#getAudio()} returns null. Default: false */
	public boolean disableAudio;
	/** The width of the drawing area in pixels, or 0 for using the available space. Default: 0 */
	public final int width;
	/** The height of the drawing area in pixels, or 0 for using the available space. Default: 0 */
	public final int height;
	/** Horizontal padding to use for resizing the game content in the browser window, for resizable applications only. The
	 * padding is necessary to prevent the browser from showing scrollbars. This can happen if the game content is of the same size
	 * as the browser window. The padding is given in logical pixels, not affected by {@link #usePhysicalPixels}. Default: 10 */
	public int padHorizontal = 10;
	/** Vertical padding to use for resizing the game content in the browser window, for resizable applications only. The
	 * padding is necessary to prevent the browser from showing scrollbars. This can happen if the game content is of the same size
	 * as the browser window. The padding is given in logical pixels, not affected by {@link #usePhysicalPixels}. Default: 10 */
    public int padVertical = 10;
	/** Whether to use a stencil buffer. Default: false */
	public boolean stencil = false;
	/** Whether to enable antialiasing. Default: false */
	public boolean antialiasing = false;
	/** The Panel to add the WebGL canvas to, can be null in which case a Panel is added automatically to the body element of the
	 * DOM. Default: null */
	public Panel rootPanel;
	/** The id of a canvas element to be used as the drawing area, can be null in which case a Panel and Canvas are added to the
	 * body element of the DOM. Default: null */
	public String canvasId;
	/** Whether to use physical device pixels or CSS pixels for scaling the canvas. Makes a difference on mobile devices and HDPI
	 * and Retina displays. Set to true for resizable and fullscreen games on mobile devices and for Desktops if you want to use
	 * the full resolution of HDPI/Retina displays.<br/>
	 * Setting to false mostly makes sense for fixed-size games or non-mobile games expecting performance issues on huge
	 * resolutions. If you target mobiles and desktops, consider using physical device pixels on mobile devices only by using the
	 * return value of {@link GwtApplication#isMobileDevice()} . Default: false */
	public final boolean usePhysicalPixels;
	/** A TextArea to log messages to. Can be null to only use JS console.log() for logging. Default: null */
	public TextArea log;
	/** Whether to use debugging mode for OpenGL calls. Errors will result in a RuntimeException being thrown. Default: false */
	public boolean useDebugGL = false;
	/** Whether to enable OpenGL ES 3.0 (aka WebGL2) if supported. If not supported it will fall back to OpenGL ES 2.0. When GL ES
	 * 3.0 is enabled, {@link com.badlogic.gdx.Gdx#gl30} can be used to access its functionality. Default: false */
	public boolean useGL30 = false;
	/** Preserve the back buffer, needed if you fetch a screenshot via canvas#toDataUrl, may have performance impact. Default: false */
	public boolean preserveDrawingBuffer = false;
	/** Whether to include an alpha channel in the color buffer to combine the color buffer with the rest of the webpage
	 * effectively allows transparent backgrounds in GWT, at a performance cost. Default: false */
	public boolean alpha = false;
	/** Whether to use premultiplied alpha; may have performance impact. Default: false */
	public boolean premultipliedAlpha = false;
	/** Screen-orientation to attempt locking as the application enters full-screen-mode. Note that on mobile browsers, full-screen
	 * mode can typically only be entered on a user gesture (click, tap, keystroke). Default: null */
	public OrientationLockType fullscreenOrientation;
	/** Whether openURI will open page in new tab. By default, it will, however it may be blocked by popup blockers.
	 * To prevent the page from being blocked you can redirect to the new page. However, this will exit your game. Default: true */
	public boolean openURLInNewWindow = true;
	/** Whether to use the accelerometer. Default: true */
	public boolean useAccelerometer = true;
	/** Whether to use the gyroscope. Default: false */
	public boolean useGyroscope = false;
	/** Whether to make the WebGL context compatible with WebXR, may have positive performance impact. Default: false */
	public boolean xrCompatible = false;

	/** Creates configuration for a resizable application, using available browser window space minus padding (see
	 * {@link #padVertical}, {@link #padHorizontal}). Will not {@link #usePhysicalPixels use physical pixels}. */
	public GwtApplicationConfiguration () {
		this(false);
	}

	/** Creates configuration for a resizable application, using available browser window space minus padding (see
	 * {@link #padVertical}, {@link #padHorizontal}). Also see {@link #usePhysicalPixels} documentation. */
	public GwtApplicationConfiguration (boolean usePhysicalPixels) {
		this(0, 0, usePhysicalPixels);
	}

	/** Creates configuration for a fixed size application. Will not {@link #usePhysicalPixels use physical pixels}. */
	public GwtApplicationConfiguration (int width, int height) {
		this(width, height, false);
	}

	/** Creates configuration for a fixed size application. Also see {@link #usePhysicalPixels} documentation. */
	public GwtApplicationConfiguration (int width, int height, boolean usePhysicalPixels) {
		this.width = width;
		this.height = height;
		this.usePhysicalPixels = usePhysicalPixels;
	}

	public boolean isFixedSizeApplication () {
		return width != 0 && height != 0;
	}
}
