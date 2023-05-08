#ifdef GL_ES
#define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

float pi = 3.14159;
float stepSize = 0.0005;
float torchAngle = 30.0 * (pi / 180.0);
float torchDistance = 5.5;
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
uniform bool u_gameover;

float get_dim(float, float, vec2);

void main() {
    u_heading;

    if (max(abs(uv.x), abs(uv.y)) > 0.8) {
        gl_FragColor = vec4(0, 0, 0, 1.0);
        return;
    }

    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);

    float dim = get_dim(u_cam_x[0], u_cam_y[0], u_heading[0]);
    float other_dim = 0.0;

    // can only see enemy torches if they are visible
    // *obviously?*
    if (dim > 0.2 && !u_gameover) {
        other_dim = 0.0;

        for (int i = 1; i < u_num_players; i++) {
            vec2 diff = vec2(u_cam_x[i], 1.0 - u_cam_y[i]) - mask_uv;
            if (diff.x * diff.x + diff.y * diff.y < 4.0 * torchDistance * torchDistance) {
                other_dim = max(other_dim, get_dim(u_cam_x[i], u_cam_y[i], u_heading[i]));
            }
        }

        // make enemy torches red-ish
        if (other_dim > 0.1) {
            gl_FragColor.gb *= 0.5;
            gl_FragColor.r += other_dim;
        }
    }

    gl_FragColor.rgb *= dim;

    // darkest colour possible = purple
    gl_FragColor.rgb = max(0.6 * vec3(0.13, 0.0667, 0.1529), gl_FragColor.rgb);

    if (u_gameover) {
        // darken colour
        gl_FragColor.rgb *= 0.3;
        gl_FragColor.r *= 2.0;
    }
}

float get_dim(float cam_x, float cam_y, vec2 heading) {
    float dim_this = 1.0;

    vec2 diff = vec2(cam_x, 1.0 - cam_y) - mask_uv;

    vec2 delta = normalize(diff) * stepSize;
    int steps = int(length(diff) / stepSize);
    float m = 1.0;

    for (int i = 0; i < steps; i++) {
        vec2 p = mask_uv + delta * float(i);
        vec4 c = texture2D(u_mask, p);
        if (c.w > 0.0) {
            m -= 0.0058 * 10.0;
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

    // torch directionality
    float d = dot(heading, normalize(diff + heading * 0.5));
    float d2 = dot(heading, normalize(diff));
    float a = acos(d);
    if (d2 < 0.5) {
        a = length(diff - heading * 0.5);
        f = min(max(1.0 - (a - 0.8) / 0.25, 0.0), f);
    }

    if (d < 0.0 || a > torchAngle) {
        f = min(max(1.0 - (a - torchAngle) / 0.2, 0.0), f);
    }

    dim_this *= max(f, 0.1);

    return dim_this;
}
