uniform sampler2D text_in;
uniform sampler2D inSampler;

void main(void)
{
  vec4 height = texture2D(text_in, gl_TexCoord[0].st);
  vec2 gradientCoord1 = vec2(0, 1.0-height.x);
  vec2 gradientCoord2 = vec2(1, 1.0-height.x);
  vec4 color1 = texture2D(inSampler, gradientCoord1);
  vec4 color2 = texture2D(inSampler, gradientCoord2);
  gl_FragColor = color1 * gl_TexCoord[0].s + color2 * (1 - gl_TexCoord[0].s);
}
