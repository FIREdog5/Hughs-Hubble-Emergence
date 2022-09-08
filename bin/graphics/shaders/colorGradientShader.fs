uniform sampler2D heightSampler;
uniform sampler2D paletteSampler;
uniform sampler2D biomeSampler;

void main(void)
{
  vec4 height = texture2D(heightSampler, gl_TexCoord[0].st);
  vec4 biome = texture2D(biomeSampler, gl_TexCoord[0].st);

  float paletteSamplerSize = textureSize(paletteSampler, 0).x;
  float ratio = mod(biome.x * paletteSamplerSize -.5,1);
  vec2 gradientCoord1 = vec2(floor(biome.x * paletteSamplerSize + .5 + mod(paletteSamplerSize,2)) / paletteSamplerSize, 1.0-height.x);
  vec2 gradientCoord2 = vec2(floor(biome.x * paletteSamplerSize - .5 + mod(paletteSamplerSize,2)) / paletteSamplerSize, 1.0-height.x);

  vec4 color1 = texture2D(paletteSampler, gradientCoord1);
  vec4 color2 = texture2D(paletteSampler, gradientCoord2);
  gl_FragColor = mix(color1, color2, ratio);
}
