#version 150

struct LightProperties
{
	vec4 position;
	vec4 ambientColor;
	vec4 diffuseColor;
	vec4 specularColor;
};

struct MaterialProperties
{
	vec4 ambientColor;
	vec4 diffuseColor;
	vec4 specularColor;
	float specularExponent;
};

uniform	LightProperties u_light;
uniform	MaterialProperties u_material;
uniform mat4 u_viewMatrix;
uniform mat4 u_modelMatrix;

uniform sampler2D u_texture;
uniform bool u_is_texture;

in vec3 v_normal;
in vec3 v_eye;
in vec2 v_texcoord;
in vec4 v_position;

out vec4 fragColor;

void main(void)
{
    vec4 color = u_light.ambientColor * u_material.ambientColor;

	vec3 normal = normalize(v_normal);

    vec3 direction = normalize(vec3((u_viewMatrix * u_modelMatrix * u_light.position) - v_position));

	float nDotL = max(dot(direction, normal), 0.0);

	if (nDotL > 0.0)
	{
        if(u_is_texture) {
            color += texture(u_texture, v_texcoord) * nDotL;
        } else {
            color += u_light.diffuseColor * u_material.diffuseColor * nDotL;
        }

        vec3 eye = normalize(v_eye);

        // Incident vector is opposite light direction vector.
        vec3 reflection = reflect(-direction, normal);

        float eDotR = max(dot(eye, reflection), 0.0);

		float specularIntensity = 0.0;
		if (eDotR > 0.0)
		{
			specularIntensity = pow(eDotR, u_material.specularExponent);
		}

		color += u_light.specularColor * u_material.specularColor * specularIntensity;
	}

	fragColor = color;
}
