#version 140
//texture
uniform sampler2D buffer_in;
// x, y, size, Renderer.unitsWide
uniform vec4 params;

void main(void)
{
  //setup
  vec2 textureSize2d = textureSize(buffer_in, 0);

  float unitsWide = params[3];
  float unitsTall = textureSize2d.y / (textureSize2d.x / unitsWide);

  float centerX = (params[0] + unitsWide / 2) / unitsWide;
  float centerY = (params[1] + unitsTall / 2) / unitsTall;
  float sizeX = params[2] / unitsWide;
  float sizeY = params[2] / unitsTall;

  //black hole resampling math
  float dist = distance(vec2(centerX, centerY) / vec2(sizeX, sizeY),gl_TexCoord[0].st / vec2(sizeX, sizeY)) * 24 * 1;
  float locX = gl_TexCoord[0].s;
  float locY = gl_TexCoord[0].t;
  if (dist <= 12) {
    float newDist = 0;
    if (dist > 4 && dist < 8) {
      newDist = -1*(1-sqrt(1-pow(dist/4 - 2, 2)))*12*4;
    } else if (dist >= 8 && dist <= 10) {
      newDist = abs(pow(10 - dist, .3333)) * -4.73 + 5.959;
    }
    else if (dist >= 8) {
      newDist = abs(pow(dist - 10, .3333)) * 4.73 + 5.959;
    }
    newDist = newDist / 12.0;
    locX = centerX + (locX - centerX) * newDist;
    locY = centerY + (locY - centerY) * newDist;
  }

  // bilinear interpolation
  vec2 ratio = vec2(locX * textureSize2d.x - floor(locX * textureSize2d.x), locY * textureSize2d.y - floor(locY * textureSize2d.y));

  vec2 lowerLeftPos = vec2(floor(locX * textureSize2d.x), floor(locY * textureSize2d.y)) / textureSize2d;
  vec2 topLeftPos = vec2(floor(locX * textureSize2d.x), floor(locY * textureSize2d.y) + 1) / textureSize2d;
  vec2 lowerRightPos = vec2(floor(locX * textureSize2d.x) + 1, floor(locY * textureSize2d.y)) / textureSize2d;
  vec2 topRightPos = vec2(floor(locX * textureSize2d.x) + 1, floor(locY * textureSize2d.y) + 1) / textureSize2d;

  vec4 lowerLeftColor = texture2D(buffer_in, lowerLeftPos);
  vec4 topLeftColor = texture2D(buffer_in, topLeftPos);
  vec4 lowerRightColor = texture2D(buffer_in, lowerRightPos);
  vec4 topRightColor = texture2D(buffer_in, topRightPos);

  vec4 topColor = mix(topLeftColor, topRightColor, ratio.x);
  vec4 lowerColor = mix(lowerLeftColor, lowerRightColor, ratio.x);

  vec4 color = mix(lowerColor, topColor, ratio.y);

  if (dist >= 12) {
    color = texture2D(buffer_in, vec2(locX, locY));
  }

  if (dist <= 4.15) {
    color = vec4(0,0,0,1);
  }

  gl_FragColor = color;
}
