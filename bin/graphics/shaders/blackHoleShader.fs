uniform sampler2D buffer_in;

void main(void)
{
  float dist = distance(vec2(0.5, 0.5),gl_TexCoord[0].st) * 24;
  float locX = gl_TexCoord[0].s;
  float locY = gl_TexCoord[0].t;
  if (dist <= 12) {
    float newDist = 0;
    if (dist > 4 && dist < 8) {
      newDist = -1*(1-sqrt(1-pow(dist/4 - 2, 2)))*12*4;
    } else if (dist >= 8 && dist <= 10) {
      newDist = pow(dist - 10, .3333) * -4.73 + 5.959;
    }
    else if (dist >= 8) {
      newDist = pow(dist - 10, .3333) * 4.73 + 5.959;
    }
    newDist = newDist / 12.0;
    // if (dist <= 2) {
    //   newDist = (1-sqrt(1-(dist/2-1) * (dist/2-1)))*12;
    // } else {
    //   newDist = (1-sqrt(1-(dist/1-2) * (dist/1-2)));
    // }
    locX = 0.5 + (locX - 0.5) * newDist;
    locY = 0.5 + (locY - 0.5) * newDist;
  }

  vec2 textureSize2d = textureSize(buffer_in, 0);

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
