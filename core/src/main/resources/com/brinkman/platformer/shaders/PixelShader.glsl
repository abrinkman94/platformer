varying vec4 vColor;
varying vec2 vTexCoord;

//texture samplers
uniform sampler2D u_texture; //diffuse map

//additional parameters for the shader
uniform vec4 ambientColor;
uniform vec2 pointerPosition; // The mouse position
uniform vec2 screenResolution; // The screen resolution
uniform vec4 pointColor; // The color and intensity of the point light

// The radius of the point light; 0.5 is a circle fitting the edges of the screen.
const float RADIUS = 0.1;
// The "fade-out" of the point light, between 0.0 and 1.0
const float SOFTNESS = 0.5;

void main() {
    // RGBA of untouched texture
    vec4 diffuseColor = texture2D(u_texture, vTexCoord);

    // Position of the light
    vec3 lightDir = vec3(pointerPosition.xy - (gl_FragCoord.xy / screenResolution.xy), 0.075);

    // Correct light position for aspect ratio
    lightDir.x *= screenResolution.x / screenResolution.y;

    // Determine attenuation of light at this pixel
    float lightDist = length(lightDir);

    // Calculate ambient intensity
    vec3 ambientLight = ambientColor.rgb * ambientColor.a;

    float attenuation = 1.0 / (3.0 * lightDist);

    vec3 intensity = ambientLight + diffuseColor.rgb * attenuation;
    vec3 finalColor = diffuseColor.rgb * intensity;
    gl_FragColor = vColor * vec4(finalColor, diffuseColor.a);
}

