#ifdef GL_ES
#define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

float stepSize = 0.0005;

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
varying vec2 uv, mask_uv;

uniform float u_cam_x, u_cam_y;
uniform float u_width, u_height;
uniform sampler2D u_texture;
uniform sampler2D u_mask;

void main() {
    u_texture;

    vec2 diff = vec2(u_cam_x, 1.0 - u_cam_y) - mask_uv;
    float len_uv = length(uv);

    vec2 delta = normalize(diff) * stepSize;
    int steps = int(length(diff) / stepSize);
    float m = 1.0;

    for (int i = 0; i < steps; i++) {
        vec2 p = mask_uv + delta * float(i);
        vec4 c = texture2D(u_mask, p);
        if (c.w > 0.0) {
            m -= 0.0575;
        }
    }

    // compensate for world stretch
    m += 0.5 * abs(dot(vec2(0.0, 1.0), normalize(abs(uv))) * (u_width / u_height));

    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);

    if (m < 1.0) {
        gl_FragColor.rgb *= m;
    }

    if (len_uv > 0.75) {
        float f = max(1.0 - (len_uv - 0.75) / 0.05, 0.0);
        gl_FragColor.rgb *= f;
    }

    gl_FragColor.rgb = max(0.5 * vec3(0.13, 0.0667, 0.1529), gl_FragColor.rgb);
}
