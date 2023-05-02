#ifdef GL_ES
#define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

float stepSize = 0.00005;
float torchAngle = 0.6981317; // 40 degrees
float torchDistance = 0.5;
float distanceFalloff = 0.2;

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
varying vec2 uv, mask_uv;

uniform float u_cam_x, u_cam_y;
uniform float u_width, u_height;
uniform vec2 u_heading;
uniform sampler2D u_texture;
uniform sampler2D u_mask;

void main() {
    u_texture;

    if (max(abs(uv.x), abs(uv.y)) > 0.8) {
        gl_FragColor = vec4(0, 0, 0, 1.0);
        return;
    }

    vec2 diff = vec2(u_cam_x, 1.0 - u_cam_y) - mask_uv;
    float len_uv = length(uv);

    vec2 delta = normalize(diff) * stepSize;
    int steps = int(length(diff) / stepSize);
    float m = 1.0;

    for (int i = 0; i < steps; i++) {
        vec2 p = mask_uv + delta * float(i);
        vec4 c = texture2D(u_mask, p);
        if (c.w > 0.0) {
            m -= 0.0058;
        }
    }

    // compensate for world stretch
    m += 0.5 * abs(dot(vec2(0.0, 1.0), normalize(abs(uv))) * (u_width / u_height));

    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);

    if (m < 1.0) {
        gl_FragColor.rgb *= min(sqrt(sqrt(m)), 1.0);
    }

    float f = 1.0;

    // maximum torch range
    if (len_uv > torchDistance) {
        f = min(max(1.0 - (len_uv - torchDistance) / distanceFalloff, 0.0), f);
    }

    // torch directionality
    float d = dot(u_heading, normalize(uv + u_heading * 0.11));
    float d2 = dot(u_heading, normalize(uv));
    float a = acos(d);
    if (d2 < -0.3) {
        a = length(uv - u_heading * 0.02);
        f = min(max(1.0 - (a - 0.08) / 0.02, 0.0), f);
    } else if (d < 0.0 || a > torchAngle) {
        f = min(max(1.0 - (a - torchAngle) / 0.12, 0.0), f);
    }

    gl_FragColor.rgb *= max(f, 0.15);


    // darkest colour possible = purple
    gl_FragColor.rgb = max(0.6 * vec3(0.13, 0.0667, 0.1529), gl_FragColor.rgb);
}
