package flora.experiments.sunflow.scenes;

import flora.experiments.sunflow.ConfigurableScene;
import flora.experiments.sunflow.RenderingConfiguration;
import flora.experiments.sunflow.RenderingKnobs;
import org.sunflow.SunflowAPI;
import org.sunflow.core.Display;
import org.sunflow.math.Matrix4;
import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;

/** Scene for a Cornell box (https://en.wikipedia.org/wiki/Cornell_box). */
public final class CornellBox extends ConfigurableScene<CornellBox> {
  private final Display display;

  public CornellBox(RenderingKnobs knobs, RenderingConfiguration configuration, Display display) {
    super(knobs, configuration, display);
    this.display = display;
  }

  @Override
  public CornellBox fromIndices(int[] indices) {
    return new CornellBox(knobs(), knobs().fromIndices(indices), display);
  }

  @Override
  protected void buildScene() {
    options(SunflowAPI.DEFAULT_OPTIONS);

    parameter(
        "transform",
        Matrix4.lookAt(new Point3(0, 0, -600), new Point3(0, 0, 0), new Vector3(0, 1, 0)));
    parameter("fov", 45.0f);
    camera("main_camera", "pinhole");
    parameter("camera", "main_camera");

    options(SunflowAPI.DEFAULT_OPTIONS);
    // cornell box
    float minX = -200;
    float maxX = 200;
    float minY = -160;
    float maxY = minY + 400;
    float minZ = -250;
    float maxZ = 200;

    float[] verts =
        new float[] {
          minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ, minX, maxY, minZ,
          maxX, maxY, minZ, maxX, maxY, maxZ, minX, maxY, maxZ,
        };
    int[] indices =
        new int[] {
          0, 1, 2, 2, 3, 0, 4, 5, 6, 6, 7, 4, 1, 2, 5, 5, 6, 2, 2, 3, 6, 6, 7, 3, 0, 3, 4, 4, 7, 3
        };

    parameter("diffuse", null, 0.70f, 0.70f, 0.70f);
    shader("gray_shader", "diffuse");
    parameter("diffuse", null, 0.80f, 0.25f, 0.25f);
    shader("red_shader", "diffuse");
    parameter("diffuse", null, 0.25f, 0.25f, 0.80f);
    shader("blue_shader", "diffuse");

    // build walls
    parameter("triangles", indices);
    parameter("points", "point", "vertex", verts);
    parameter("faceshaders", new int[] {0, 0, 0, 0, 1, 1, 0, 0, 2, 2});
    geometry("walls", "triangle_mesh");

    // instance walls
    parameter("shaders", new String[] {"gray_shader", "red_shader", "blue_shader"});
    instance("walls.instance", "walls");

    // create mesh light
    parameter(
        "points",
        "point",
        "vertex",
        new float[] {-50, maxY - 1, -50, 50, maxY - 1, -50, 50, maxY - 1, 50, -50, maxY - 1, 50});
    parameter("triangles", new int[] {0, 1, 2, 2, 3, 0});
    parameter("radiance", null, 15, 15, 15);
    parameter("samples", 8);
    light("light", "triangle_mesh");

    // spheres
    parameter("eta", 1.6f);
    shader("Glass", "glass");
    sphere("glass_sphere", "Glass", -120, minY + 55, -150, 50);
    parameter("color", null, 0.70f, 0.70f, 0.70f);
    shader("Mirror", "mirror");
    sphere("mirror_sphere", "Mirror", 100, minY + 60, -50, 50);

    // scanned model
    geometry("teapot", "teapot");
    parameter(
        "transform",
        Matrix4.translation(80, -50, 100)
            .multiply(Matrix4.rotateX((float) -Math.PI / 6))
            .multiply(Matrix4.rotateY((float) Math.PI / 4))
            .multiply(Matrix4.rotateX((float) -Math.PI / 2).multiply(Matrix4.scale(1.2f))));
    parameter("shaders", "gray_shader");
    instance("teapot.instance1", "teapot");
    parameter(
        "transform",
        Matrix4.translation(-80, -160, 50)
            .multiply(Matrix4.rotateY((float) Math.PI / 4))
            .multiply(Matrix4.rotateX((float) -Math.PI / 2).multiply(Matrix4.scale(1.2f))));
    parameter("shaders", "gray_shader");
    instance("teapot.instance2", "teapot");
  }

  private void sphere(String name, String shaderName, float x, float y, float z, float radius) {
    geometry(name, "sphere");
    parameter("transform", Matrix4.translation(x, y, z).multiply(Matrix4.scale(radius)));
    parameter("shaders", shaderName);
    instance(name + ".instance", name);
  }
}
