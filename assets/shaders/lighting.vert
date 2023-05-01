attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform float u_cam_x, u_cam_y;
uniform float u_width, u_height;

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 uv, mask_uv;

void main() {
    u_width;
    u_height;

    v_color = a_color;
    v_color.a = v_color.a * (255.0/254.0);
    v_texCoords = a_texCoord0;
    gl_Position = u_projTrans * a_position;
    uv = gl_Position.xy;
    mask_uv = a_position.xy / vec2(u_width, u_height);
    mask_uv.y = 1.0 - mask_uv.y;
}