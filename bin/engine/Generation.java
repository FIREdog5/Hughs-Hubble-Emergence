package bin.engine;

import bin.world.Star;
import bin.world.Planet;
import bin.world.World;
import bin.resource.Palettes;

public class Generation {
  public static void generateStars(World world, int count) {
    for (int i = 0; i < count; i++) {
      float x = (int) (Math.random() * 20000 - 10000);
      float y = (int) (Math.random() * 20000 - 10000);
      world.addWorldObject(new Planet(x, y, Palettes.earth1));
    }
    world.addWorldObject(new Planet(0, 0, Palettes.earth1));
    world.addWorldObject(new Planet(20, 20, Palettes.earth1));
    world.addWorldObject(new Planet(-20, 20, Palettes.earth1));
    world.addWorldObject(new Planet(-20, -20, Palettes.earth1));
    world.addWorldObject(new Planet(20, -20, Palettes.earth1));
  }
}
