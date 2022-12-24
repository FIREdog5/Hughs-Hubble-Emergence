uniform sampler2D heightSampler;
uniform sampler2D paletteSampler;
uniform sampler2D biomeSampler;

vec4 sample(float x, float y);

void main(void)
{
  // bilinear interpolation

  vec2 textureSize2d = textureSize(heightSampler, 0);

  float locX = gl_TexCoord[0].s;
  float locY = gl_TexCoord[0].t;
  vec2 ratio = vec2(locX * textureSize2d.x - floor(locX * textureSize2d.x), locY * textureSize2d.y - floor(locY * textureSize2d.y));

  vec2 lowerLeftPos = vec2(floor(locX * textureSize2d.x), floor(locY * textureSize2d.y)) / textureSize2d;
  vec2 topLeftPos = vec2(floor(locX * textureSize2d.x), floor(locY * textureSize2d.y) + 1) / textureSize2d;
  vec2 lowerRightPos = vec2(floor(locX * textureSize2d.x) + 1, floor(locY * textureSize2d.y)) / textureSize2d;
  vec2 topRightPos = vec2(floor(locX * textureSize2d.x) + 1, floor(locY * textureSize2d.y) + 1) / textureSize2d;

  vec4 lowerLeftColor = sample(lowerLeftPos.x, lowerLeftPos.y);
  vec4 topLeftColor = sample(topLeftPos.x, topLeftPos.y);
  vec4 lowerRightColor = sample(lowerRightPos.x, lowerRightPos.y);
  vec4 topRightColor = sample(topRightPos.x, topRightPos.y);

  vec4 topColor = mix(topLeftColor, topRightColor, ratio.x);
  vec4 lowerColor = mix(lowerLeftColor, lowerRightColor, ratio.x);

  vec4 color = mix(lowerColor, topColor, ratio.y);

  gl_FragColor = color;
}

vec4 sample(float x, float y)
{
  vec4 height = texture2D(heightSampler, vec2(x,y));
  vec4 biome = texture2D(biomeSampler, vec2(x,y));

  float paletteSamplerSize = textureSize(paletteSampler, 0).x;
  float ratio = mod(biome.x * paletteSamplerSize -.5,1);
  vec2 gradientCoord1 = vec2(floor(biome.x * paletteSamplerSize + .5 + mod(paletteSamplerSize,2)) / paletteSamplerSize, 1.0-height.x);
  vec2 gradientCoord2 = vec2(floor(biome.x * paletteSamplerSize - .5 + mod(paletteSamplerSize,2)) / paletteSamplerSize, 1.0-height.x);

  vec4 color1High = texture2D(paletteSampler, vec2(gradientCoord1.x, (floor(gradientCoord1.y * 256)) / 265));
  vec4 color1Low = texture2D(paletteSampler, vec2(gradientCoord1.x, (floor(gradientCoord1.y * 256) + 1) / 265));
  vec4 color1 = mix(color1High, color1Low, gradientCoord1.y * 256 - floor(gradientCoord1.y * 256));
  vec4 color2High = texture2D(paletteSampler, vec2(gradientCoord2.x, (floor(gradientCoord2.y * 256)) / 265));
  vec4 color2Low = texture2D(paletteSampler, vec2(gradientCoord2.x, (floor(gradientCoord2.y * 256) + 1) / 265));
  vec4 color2 = mix(color2High, color2Low, gradientCoord2.y * 256 - floor(gradientCoord2.y * 256));

  return mix(color2, color1, ratio);
}
