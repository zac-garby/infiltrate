#ifdef GL_ES
#define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

float pi = 3.14159;
float stepSize = 0.00005;
float torchAngle = 30.0 * (pi / 180.0); // 40 degrees
float torchDistance = 5.0;
float distanceFalloff = 0.5;

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
varying vec2 uv, mask_uv;

uniform int u_num_players;
uniform float u_cam_x[5], u_cam_y[5];
uniform float u_width, u_height;
uniform vec2 u_heading[5];
uniform sampler2D u_texture;
uniform sampler2D u_mask;

void main() {
    u_heading;

    if (max(abs(uv.x), abs(uv.y)) > 0.8) {
        gl_FragColor = vec4(0, 0, 0, 1.0);
        return;
    }

    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
    float dim = 0.0;

    for (int i = 0; i < u_num_players; i++) {
        float dim_this = 1.0;

        vec2 diff = vec2(u_cam_x[i], 1.0 - u_cam_y[i]) - mask_uv;

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
        m += 0.5 * abs(dot(vec2(0.0, 1.0), normalize(abs(diff))) * (u_width / u_height));

        // apply shadows
        if (m < 1.0) {
            dim_this *= max(min(sqrt(sqrt(m)), 1.0), 0.0);
        }

        // calculate torch: ...
        float f = 1.0;
        diff.x *= -u_width / 12.0;
        diff.y *= u_height / 12.0;

        // maximum torch range
        float len_diff = length(diff);
        if (len_diff > torchDistance - distanceFalloff) {
            f = min(max(1.0 - (len_diff - torchDistance) / distanceFalloff, 0.0), f);
        }

        vec2 heading = u_heading[i];

        // torch directionality
        float d = dot(heading, normalize(diff + heading * 0.5));
        float d2 = dot(heading, normalize(diff));
        float a = acos(d);
        if (d2 < 0.5) {
            a = length(diff - heading * 0.5);
            f = min(max(1.0 - (a - 0.4) / 0.35, 0.0), f);
        } else if (d < 0.0 || a > torchAngle) {
            f = min(max(1.0 - (a - torchAngle) / 0.2, 0.0), f);
        }

        dim_this *= max(f, 0.15);
        dim = max(dim, dim_this);
    }

    gl_FragColor.rgb *= dim;

    // darkest colour possible = purple
    gl_FragColor.rgb = max(0.6 * vec3(0.13, 0.0667, 0.1529), gl_FragColor.rgb);
}
