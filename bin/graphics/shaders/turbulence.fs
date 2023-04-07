#version 130
// TODO a fragment shader that takes a texture of r,b components and uses them as domain warp of a noise map. used as paralax effect for turbulent noise. feed sine wave base to create stripes.
uniform sampler2D heightSampler;
uniform sampler2D warpSampler;
uniform float offset;

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
 int textureHeight = textureSize(heightSampler, 0).y;

 float xNew = x + offset;
 vec2 warp = texture2D(warpSampler, vec2(xNew,y)).xz /25;
 if (warp.y + y > textureHeight) {
   warp.y = textureHeight - y;
 }
 if (warp.y + y < 0) {
   warp.y = 0 - y;
 }
 vec4 height = texture2D(heightSampler, vec2(x,y) + warp);
 return height;
}
