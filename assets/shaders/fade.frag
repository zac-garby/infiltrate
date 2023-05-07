#version 120

#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_fade = 1.0;

void main() {
    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
    gl_FragColor.rgb *= u_fade;
}
