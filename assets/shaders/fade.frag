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
uniform float u_fade;

void main() {
    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);

    // apply fade
    gl_FragColor.rgb *= u_fade;

    // darkest colour possible = purple
    gl_FragColor.rgb = max(0.6 * vec3(0.13, 0.0667, 0.1529), gl_FragColor.rgb);
}
