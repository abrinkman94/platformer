varying vec4 vColor;
varying vec2 vTexCoord;

// Texture sampler(s)
uniform sampler2D u_texture; // Diffuse map (basically samples the untouched "regular" texture)
uniform sampler2D u_normals; // Normal map

uniform vec2 screenResolution; // The resolution of the display port
uniform vec4 ambientColor; // Color and intensity of ambient light
uniform vec3 pointLightPosition; // Position of the light
uniform vec4 pointLightColor; // Color and intensity of the point light
uniform vec3 pointLightFalloff; // Attenuation coefficients

// The main shader routine
void main() {
    // RGBA of untouched texture
    vec4 diffuseColor = texture2D(u_texture, vTexCoord);

    // RGBA of normal map
    vec4 normalMap = texture2D(u_normals, vTexCoord);

    // Delta position of the light // TODO Can gl_FragCoord be optimized away?  Could be expensive.
    vec3 lightDirection = vec3(pointLightPosition.xy - (gl_FragCoord.xy / screenResolution.xy), pointLightPosition.z);

    // Correct light position for aspect ratio
    lightDirection.x *= screenResolution.x / screenResolution.y;

    // Determine distance from light (used for attenuation).  Must be done before normalizing the light direction.
    float lightDistance = length(lightDirection);

    // Normalize the light vector
    vec3 l = normalize(lightDirection);
    // Normalize the normal map
    vec3 n = normalize(normalMap.rgb * 2.0 - 1.0);

    // Pre-multiply light color w/ intensity
    vec3 light = pointLightColor.rgb * pointLightColor.a;
    vec3 ambient = ambientColor.rgb * ambientColor.a;

    // Perform "N dot L" to determine diffuse color
    // TODO Fix when normals are working correctly.
    vec3 diffuse = light;// * max(dot(n, l), 0.0);

    // Calculate attenuation
    float attenuation = 1.0 / (pointLightFalloff.x + (pointLightFalloff.y * lightDistance) + (pointLightFalloff.z * lightDistance * lightDistance));

    // Get total intensity
    vec3 intensity = ambient + diffuse * attenuation;
    vec3 finalColor = diffuseColor.rgb * intensity;

    gl_FragColor = vColor * vec4(finalColor.rgb, diffuseColor.a);
}

