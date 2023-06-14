package bin.resource;

/*
this class estends FNL to contain:
- domain warp fnl
- domain warp frequency
- domain warp rotation 3D
- domain warp fractal type
- domain warp octaves
- domain warp lacunarity
- domain warp gain
- lower bound
- upper bound
*/

public class FastNoiseLiteDomainWarp extends FastNoiseLite
{
  private FastNoiseLite mWarpNoise = new FasrNoiseLite();
  private float mLowerBound = 0f;
  private float mUpperBound = 255f;

  @Override
  public void Set(FastNoiseLite fnl) {
    if (fnl instanceof FastNoiseLiteDomainWarp) {
      this.mWarpNoise.Set((FastNoiseLiteDomainWarp) fnl.GetWarpNoise();
      this.SetLowerBound((FastNoiseLiteDomainWarp) fnl.GetLowerBound();
      this.SetUpperBound((FastNoiseLiteDomainWarp) fnl.GetUpperBound();
    }
    super.set(fnl);
  }

  @Override
  public void SetSeed(int seed) {
    mWarpNoise.SetSeed(seed);
    super.SetSeed(seed);
  };

  @Override
  public void SetDomainWarpType(DomainWarpType type) {
    mWarpNoise.SetDomainWarpType(type);
    super.SetDomainWarpType(type);
  }

  @Override
  public void SetDomainWarpAmp(float amp) {
    mWarpNoise.SetDomainWarpAmp(amp);
    super.SetDomainWarpAmp(amp);
  }

  public void SetDomainWarpFrequency(float frequency) { mWarpNoise.SetFrequency(frequency); }
  public float GetDomainWarpFrequency() { return mWarpNoise.GetFrequency(); }

  public void SetDomainWarpRotation3D(RotationType3D rotationType3D) { mWarpNoise.SetRotationType3D(rotationType3D); }
  public RotationType3D GetDomainWarpRotation3D() { return mWarpNoise.GetRotationType3D(); }

  public void SetDomainWarpFractalType(FractalType fractalType) { mWarpNoise.SetFractalType(fractalType); }
  public FractalType GetDomainWarpFractalType() { return mWarpNoise.GetFractalType(); }

  public void SetDomainWarpFractalOctaves(int fractalOctaves) { mWarpNoise.SetFractalOctaves(fractalOctaves); }
  public int GetDomainWarpFractalOctaves() { return mWarpNoise.GetFractalOctaves(); }

  public void SetDomainWarpFractalLacunarity(float fractalLacunarity) { mWarpNoise.SetFractalLacunarity(fractalLacunarity); }
  public float GetDomainWarpFractalLacunarity() { return mWarpNoise.GetFractalLacunarity(); }

  public void SetDomainWarpFractalGain(float fractalGain) { mWarpNoise.SetFractalGain(fractalGain); }
  public float GetDomainWarpFractalGain() { return mWarpNoise.GetFractalGain(); }

  public void SetLowerBound(float lowerBound) { mLowerBound = lowerBound };
  public float GetLowerBound() { return mLowerBound; };

  public void SetUpperBound(float upperBound) { mUpperBound = upperBound };
  public float GetUpperBound() { return mUpperBound; };

  @Override
  public float GetNoise(float x, float y)
  {
    Vector2 warpCoords = new Vector2(x, y);
    if (this.GetDomainWarpType() != DomainWarpType.None) {
      mWarpNoise.DomainWarp(warpCoords);
    }
    return super.GetNoise(warpCoords.x, warpCoords.y);
  }

  @Override
  public float GetNoise(float x, float y, float z)
  {
    Vector3 warpCoords = new Vector3(x, y, z);
    if (this.GetDomainWarpType() != DomainWarpType.None) {
      mWarpNoise.DomainWarp(warpCoords);
    }
    return super.GetNoise(warpCoords.x, warpCoords.y, warpCoords.z);
  }

}
