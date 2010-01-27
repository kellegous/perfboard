package kellegous.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;

public class CanvasElement extends Element {
  public static class Context extends JavaScriptObject {
    public final native void arc(double x, double y, double radius, double startAngle, double endAngle, boolean counterClockwise) /*-{
      this.arc(x, y, radius, startAngle, endAngle, counterClockwise);
    }-*/;

    public final native void beginPath() /*-{
      this.beginPath();
    }-*/;

    public final native void clearRect(double x, double y, double width, double height) /*-{
      this.clearRect(x, y, width, height);
    }-*/;

    public final native void clip() /*-{
      this.clip();
    }-*/;

    public final native void closePath() /*-{
      this.closePath();
    }-*/;

    public final native void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y) /*-{
      this.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
    }-*/;

    public final native void drawImage(ImageElement img, double srcX, double srcY, double srcWidth, double srcHeight, double dstX, double dstY, double dstWidth, double dstHeight) /*-{
      this.drawImage(img, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);
    }-*/;

    public final native void fill() /*-{
      this.fill();
    }-*/;

    public final native void fillRect(double x, double y, double width, double height) /*-{
      this.fillRect(x, y, width, height);
    }-*/;

    public final native void lineTo(double x, double y) /*-{
      this.lineTo(x, y);
    }-*/;

    public final native void moveTo(double x, double y) /*-{
      this.moveTo(x, y);
    }-*/;

    public final native void quadraticCurveTo(double cpx, double cpy, double x, double y) /*-{
      this.quadraticCurveTo(cpx, cpy, x, y);
    }-*/;

    public final native void rect(double x, double y, double width, double height) /*-{
      this.rect(x, y, width, height);
    }-*/;

    public final native void restore() /*-{
      this.restore();
    }-*/;

    public final native void rotate(double angle) /*-{
      this.rotate(angle);
    }-*/;

    public final native void save() /*-{
      this.save();
    }-*/;

    public final native void scale(double x, double y) /*-{
      this.scale(x, y);
    }-*/;

    public final native void setFillStyle(String color) /*-{
      this.fillStyle = color;
    }-*/;

    public final native void setGlobalAlpha(double alpha) /*-{
      this.globalAlpha = alpha;
    }-*/;

    public final native void setLineWidth(double width) /*-{
      this.lineWidth = width;
    }-*/;

    public final native void setStrokeStyle(String color) /*-{
      this.strokeStyle = color;
    }-*/;

    public final native void stroke() /*-{
      this.stroke();
    }-*/;

    public final native void strokeRect(double x, double y, double width, double height) /*-{
      this.strokeRect(x, y, width, height);
    }-*/;

    public final native void transform(double m11, double m12, double m21, double m22, double dx, double dy) /*-{
      this.transform(m11, m12, m21, m22, dx, dy);
    }-*/;

    public final native void translate(double x, double y) /*-{
      this.translate(x, y);
    }-*/;

    protected Context() {
    }
  }

  public final int width() {
    return Integer.parseInt(getAttribute("width"));
  }

  public final int height() {
    return Integer.parseInt(getAttribute("height"));
  }

  public final native Context context() /*-{
    return this.getContext('2d');
  }-*/;

  public static CanvasElement create(Document document, int width, int height) {
    final Element e = document.createElement("canvas");
    e.setAttribute("width", "" + width);
    e.setAttribute("height", "" + height);
    return e.cast();
  }
  
  public static CanvasElement create(Document document) {
    return document.createElement("canvas").cast();
  }

  protected CanvasElement() {
  }
}
