precision mediump sampler2D;
precision highp float;
uniform vec3 _AE_DIRECTIONAL_LIGHTS_DIRECTION_[1];
uniform vec3 _AE_DIRECTIONAL_LIGHTS_COLOR_[1];
uniform float _AE_DIRECTIONAL_LIGHTS_INTENSITY_[1];
uniform mat4 _AE_DIRECTIONAL_LIGHT0_SHADOW_MATRIX_;
uniform float _AE_DIRECTIONAL_LIGHT0_SHADOW_ENABLED_;
uniform float _AE_DIRECTIONAL_LIGHT0_SHADOW_BIAS_;
uniform float _AE_DIRECTIONAL_LIGHT0_SHADOW_STRENGTH_;
uniform float _AE_DIRECTIONAL_LIGHT0_SHADOW_SOFT_;
uniform float _AE_DIRECTIONAL_LIGHT0_SHADOW_SOFTNESS_;
uniform sampler2D _AE_DIRECTIONAL_LIGHT0_SHADOW_TEXTURE_;
uniform vec2 _AE_DIRECTIONAL_LIGHT0_SHADOW_TEXTURE_SIZE_;
uniform sampler2D u_BRDFTex;
uniform sampler2D u_RadianceTex;
uniform sampler2D u_IrradianceTex;
uniform sampler2D u_BaseColor;
uniform float u_Metallic;
uniform float u_Roughness;
uniform float u_AmbientOcclusion;
uniform sampler2D u_MRAOTex;
uniform float u_Specular;
uniform sampler2D u_SHTex;
uniform float u_ParallaxFactor;
uniform vec3 u_EmissiveColor;
uniform float u_Translucency;
uniform sampler2D u_NormalTex;
uniform vec3 g_unif_CameraWorldPos;
varying vec3 g_vary_WorldPosition;
varying vec3 g_vary_WorldNormal;
varying vec3 g_vary_WorldTangent;
varying vec3 g_vary_WorldBitangent;
varying vec2 g_vary_Texcoord;
void main ()
{
  // #if 0
  vec3 final_color_1;
  vec3 tmpvar_2[1];
  vec3 tmpvar_3[1];
  float tmpvar_4[1];
  vec2 tmpvar_5;
  mat3 tmpvar_6;
  tmpvar_6[0] = normalize(g_vary_WorldTangent);
  tmpvar_6[1] = normalize(g_vary_WorldBitangent);
  tmpvar_6[2] = normalize(g_vary_WorldNormal);
  vec3 tmpvar_7;
  tmpvar_7 = normalize((g_unif_CameraWorldPos - g_vary_WorldPosition));
  tmpvar_5.x = g_vary_Texcoord.x;
  tmpvar_5.y = (1.0 - g_vary_Texcoord.y);
  float tmpvar_8;
  float tmpvar_9;
  float tmpvar_10;
  float tmpvar_11;
  float tmpvar_12;
  tmpvar_8 = 0.5;
  tmpvar_9 = 0.5;
  tmpvar_10 = 0.5;
  tmpvar_11 = 1.0;
  tmpvar_12 = 1.0;
  vec3 tmpvar_13;
  tmpvar_13 = normalize((tmpvar_7 * tmpvar_6));
  float pixel_depth_14;
  vec2 curr_uv_15;
  float curr_layer_depth_16;
  vec2 d_uv_17;
  float d_layer_depth_18;
  float tmpvar_19;
  tmpvar_19 = mix (10.0, 5.0, clamp (abs(tmpvar_13.z), 0.0, 1.0));
  float tmpvar_20;
  tmpvar_20 = (1.0/(tmpvar_19));
  d_layer_depth_18 = tmpvar_20;
  vec2 tmpvar_21;
  tmpvar_21 = ((tmpvar_13.xy * u_ParallaxFactor) / tmpvar_19);
  d_uv_17 = tmpvar_21;
  curr_layer_depth_16 = 0.0;
  curr_uv_15 = tmpvar_5;
  pixel_depth_14 = texture2D (u_SHTex, tmpvar_5).y;
  while (true) {
    if ((curr_layer_depth_16 >= pixel_depth_14)) {
      break;
    };
    curr_uv_15 = (curr_uv_15 - d_uv_17);
    pixel_depth_14 = texture2D (u_SHTex, curr_uv_15).y;
    curr_layer_depth_16 = (curr_layer_depth_16 + d_layer_depth_18);
  };
  vec2 tmpvar_22;
  tmpvar_22 = (curr_uv_15 + tmpvar_21);
  float tmpvar_23;
  tmpvar_23 = (pixel_depth_14 - curr_layer_depth_16);
  vec2 tmpvar_24;
  tmpvar_24 = mix (curr_uv_15, tmpvar_22, (tmpvar_23 / (tmpvar_23 - 
    ((texture2D (u_SHTex, tmpvar_22).y - curr_layer_depth_16) + tmpvar_20)
  )));
  vec3 tmpvar_25;
  tmpvar_25 = normalize((tmpvar_6 * (
    (texture2D (u_NormalTex, tmpvar_24).xyz * 2.0)
   - 1.0)));
  vec4 tmpvar_26;
  tmpvar_26 = texture2D (u_BaseColor, tmpvar_24);
  tmpvar_11 = (u_Translucency * tmpvar_26.w);
  vec4 tmpvar_27;
  tmpvar_27 = texture2D (u_MRAOTex, tmpvar_24);
  tmpvar_10 = (u_Metallic * tmpvar_27.x);
  tmpvar_9 = (u_Roughness * tmpvar_27.y);
  tmpvar_12 = (u_AmbientOcclusion * tmpvar_27.z);
  tmpvar_8 = (u_Specular * texture2D (u_SHTex, tmpvar_24).x);
  vec3 tmpvar_28[1];
  vec3 tmpvar_29[1];
  float tmpvar_30[1];
  tmpvar_28[0]=tmpvar_2[0];
  tmpvar_29[0]=tmpvar_3[0];
  tmpvar_30[0]=tmpvar_4[0];
  tmpvar_28[0] = normalize(-(_AE_DIRECTIONAL_LIGHTS_DIRECTION_[0]));
  tmpvar_29[0] = _AE_DIRECTIONAL_LIGHTS_COLOR_[0];
  tmpvar_30[0] = _AE_DIRECTIONAL_LIGHTS_INTENSITY_[0];
  vec3 l_31;
  l_31 = tmpvar_28[0];
  float tmpvar_32;
  float shadow_factor_33;
  float bias_34;
  vec3 shadow_coord_35;
  if ((_AE_DIRECTIONAL_LIGHT0_SHADOW_ENABLED_ < 1.0)) {
    tmpvar_32 = 1.0;
  } else {
    float tmpvar_36;
    tmpvar_36 = max (dot (tmpvar_25, l_31), 0.0);
    vec4 tmpvar_37;
    tmpvar_37.w = 1.0;
    tmpvar_37.xyz = g_vary_WorldPosition;
    vec4 tmpvar_38;
    tmpvar_38 = (_AE_DIRECTIONAL_LIGHT0_SHADOW_MATRIX_ * tmpvar_37);
    vec3 tmpvar_39;
    tmpvar_39 = (tmpvar_38.xyz / tmpvar_38.w);
    shadow_coord_35 = tmpvar_39;
    if ((((
      (((tmpvar_39.x < 0.0) || (1.0 < tmpvar_39.x)) || (tmpvar_39.y < 0.0))
     || 
      (1.0 < tmpvar_39.y)
    ) || (tmpvar_39.z < 0.0)) || (1.0 < tmpvar_39.z))) {
      tmpvar_32 = 1.0;
    } else {
      float tmpvar_40;
      tmpvar_40 = (1.570796 - (sign(tmpvar_36) * (1.570796 - 
        (sqrt((1.0 - abs(tmpvar_36))) * (1.570796 + (abs(tmpvar_36) * (-0.2146018 + 
          (abs(tmpvar_36) * (0.08656672 + (abs(tmpvar_36) * -0.03102955)))
        ))))
      )));
      float tmpvar_41;
      tmpvar_41 = clamp ((_AE_DIRECTIONAL_LIGHT0_SHADOW_BIAS_ * (
        sin(tmpvar_40)
       / 
        cos(tmpvar_40)
      )), 0.0, 1.0);
      bias_34 = tmpvar_41;
      shadow_factor_33 = 0.0;
      if ((0.0 < _AE_DIRECTIONAL_LIGHT0_SHADOW_SOFT_)) {
        vec2 inv_tex_size_43;
        float shadow_sum_44;
        vec2 sample_offsets_45[9];
        sample_offsets_45[0] = vec2(-1.0, -1.0);
        sample_offsets_45[1] = vec2(0.0, -1.0);
        sample_offsets_45[2] = vec2(1.0, -1.0);
        sample_offsets_45[3] = vec2(-1.0, 0.0);
        sample_offsets_45[4] = vec2(0.0, 0.0);
        sample_offsets_45[5] = vec2(1.0, 0.0);
        sample_offsets_45[6] = vec2(-1.0, 1.0);
        sample_offsets_45[7] = vec2(0.0, 1.0);
        sample_offsets_45[8] = vec2(1.0, 1.0);
        shadow_sum_44 = 0.0;
        inv_tex_size_43 = (1.0/(_AE_DIRECTIONAL_LIGHT0_SHADOW_TEXTURE_SIZE_));
        for (highp int i_42 = 0; i_42 < 9; i_42++) {
          shadow_sum_44 = (shadow_sum_44 + float((shadow_coord_35.z < 
            (dot (texture2D (_AE_DIRECTIONAL_LIGHT0_SHADOW_TEXTURE_, (shadow_coord_35.xy + (
              (sample_offsets_45[i_42] * _AE_DIRECTIONAL_LIGHT0_SHADOW_SOFTNESS_)
             * inv_tex_size_43))), vec4(5.960464e-08, 1.525879e-05, 0.00390625, 1.0)) + bias_34)
          )));
        };
        shadow_factor_33 = (shadow_sum_44 / 9.0);
      } else {
        shadow_factor_33 = float((tmpvar_39.z < (
          dot (texture2D (_AE_DIRECTIONAL_LIGHT0_SHADOW_TEXTURE_, tmpvar_39.xy), vec4(5.960464e-08, 1.525879e-05, 0.00390625, 1.0))
         + tmpvar_41)));
      };
      if ((shadow_factor_33 < 1.0)) {
        shadow_factor_33 = mix (1.0, shadow_factor_33, _AE_DIRECTIONAL_LIGHT0_SHADOW_STRENGTH_);
      };
      tmpvar_32 = shadow_factor_33;
    };
  };
  vec3 tmpvar_46;
  float tmpvar_47;
  float tmpvar_48;
  tmpvar_48 = (min (abs(
    (tmpvar_25.x / tmpvar_25.z)
  ), 1.0) / max (abs(
    (tmpvar_25.x / tmpvar_25.z)
  ), 1.0));
  float tmpvar_49;
  tmpvar_49 = (tmpvar_48 * tmpvar_48);
  tmpvar_49 = (((
    ((((
      ((((-0.01213232 * tmpvar_49) + 0.05368138) * tmpvar_49) - 0.1173503)
     * tmpvar_49) + 0.1938925) * tmpvar_49) - 0.3326756)
   * tmpvar_49) + 0.9999793) * tmpvar_48);
  tmpvar_49 = (tmpvar_49 + (float(
    (abs((tmpvar_25.x / tmpvar_25.z)) > 1.0)
  ) * (
    (tmpvar_49 * -2.0)
   + 1.570796)));
  tmpvar_47 = (tmpvar_49 * sign((tmpvar_25.x / tmpvar_25.z)));
  if ((abs(tmpvar_25.z) > (1e-08 * abs(tmpvar_25.x)))) {
    if ((tmpvar_25.z < 0.0)) {
      if ((tmpvar_25.x >= 0.0)) {
        tmpvar_47 += 3.141593;
      } else {
        tmpvar_47 = (tmpvar_47 - 3.141593);
      };
    };
  } else {
    tmpvar_47 = (sign(tmpvar_25.x) * 1.570796);
  };
  vec2 tmpvar_50;
  tmpvar_50.x = (((tmpvar_47 / 3.141593) + 1.0) * 0.5);
  tmpvar_50.y = ((1.570796 - (
    sign(tmpvar_25.y)
   * 
    (1.570796 - (sqrt((1.0 - 
      abs(tmpvar_25.y)
    )) * (1.570796 + (
      abs(tmpvar_25.y)
     * 
      (-0.2146018 + (abs(tmpvar_25.y) * (0.08656672 + (
        abs(tmpvar_25.y)
       * -0.03102955))))
    ))))
  )) / 3.141593);
  tmpvar_46 = texture2D (u_IrradianceTex, tmpvar_50).xyz;
  vec2 radianceCoords_51;
  vec3 I_52;
  I_52 = -(tmpvar_7);
  vec3 tmpvar_53;
  tmpvar_53 = normalize((I_52 - (2.0 * 
    (dot (tmpvar_25, I_52) * tmpvar_25)
  )));
  float tmpvar_54;
  float tmpvar_55;
  tmpvar_55 = (min (abs(
    (tmpvar_53.x / tmpvar_53.z)
  ), 1.0) / max (abs(
    (tmpvar_53.x / tmpvar_53.z)
  ), 1.0));
  float tmpvar_56;
  tmpvar_56 = (tmpvar_55 * tmpvar_55);
  tmpvar_56 = (((
    ((((
      ((((-0.01213232 * tmpvar_56) + 0.05368138) * tmpvar_56) - 0.1173503)
     * tmpvar_56) + 0.1938925) * tmpvar_56) - 0.3326756)
   * tmpvar_56) + 0.9999793) * tmpvar_55);
  tmpvar_56 = (tmpvar_56 + (float(
    (abs((tmpvar_53.x / tmpvar_53.z)) > 1.0)
  ) * (
    (tmpvar_56 * -2.0)
   + 1.570796)));
  tmpvar_54 = (tmpvar_56 * sign((tmpvar_53.x / tmpvar_53.z)));
  if ((abs(tmpvar_53.z) > (1e-08 * abs(tmpvar_53.x)))) {
    if ((tmpvar_53.z < 0.0)) {
      if ((tmpvar_53.x >= 0.0)) {
        tmpvar_54 += 3.141593;
      } else {
        tmpvar_54 = (tmpvar_54 - 3.141593);
      };
    };
  } else {
    tmpvar_54 = (sign(tmpvar_53.x) * 1.570796);
  };
  vec2 tmpvar_57;
  tmpvar_57.x = (((tmpvar_54 / 3.141593) + 1.0) * 0.5);
  tmpvar_57.y = ((1.570796 - (
    sign(tmpvar_53.y)
   * 
    (1.570796 - (sqrt((1.0 - 
      abs(tmpvar_53.y)
    )) * (1.570796 + (
      abs(tmpvar_53.y)
     * 
      (-0.2146018 + (abs(tmpvar_53.y) * (0.08656672 + (
        abs(tmpvar_53.y)
       * -0.03102955))))
    ))))
  )) / 3.141593);
  float tmpvar_58;
  tmpvar_58 = exp2(floor((tmpvar_9 * 6.0)));
  radianceCoords_51.x = (tmpvar_57.x / tmpvar_58);
  radianceCoords_51.y = ((tmpvar_57.y / (tmpvar_58 * 2.0)) + ((tmpvar_58 - 1.0) / tmpvar_58));
  vec2 tmpvar_59;
  vec2 tmpvar_60;
  tmpvar_60.x = max (0.0, dot (tmpvar_25, tmpvar_7));
  tmpvar_60.y = (1.0 - tmpvar_9);
  tmpvar_59 = texture2D (u_BRDFTex, tmpvar_60).xy;
  tmpvar_2[0]=tmpvar_28[0];
  tmpvar_3[0]=tmpvar_29[0];
  tmpvar_4[0]=tmpvar_30[0];
  vec3 tmpvar_61;
  tmpvar_61 = mix (vec3((0.08 * tmpvar_8)), tmpvar_26.xyz, tmpvar_10);
  vec3 tmpvar_62;
  tmpvar_62 = tmpvar_28[0];
  float tmpvar_63;
  tmpvar_63 = dot (tmpvar_25, tmpvar_62);
  float tmpvar_64;
  tmpvar_64 = dot (tmpvar_25, tmpvar_7);
  float tmpvar_65;
  tmpvar_65 = dot (tmpvar_62, tmpvar_7);
  float tmpvar_66;
  tmpvar_66 = inversesqrt((2.0 + (2.0 * tmpvar_65)));
  float tmpvar_67;
  tmpvar_67 = clamp (((tmpvar_63 + tmpvar_64) * tmpvar_66), 0.0, 1.0);
  float tmpvar_68;
  tmpvar_68 = clamp (tmpvar_63, 0.0, 1.0);
  float tmpvar_69;
  tmpvar_69 = clamp (abs(tmpvar_64), 0.0, 1.0);
  float tmpvar_70;
  tmpvar_70 = (tmpvar_9 * tmpvar_9);
  float tmpvar_71;
  tmpvar_71 = (tmpvar_70 * tmpvar_70);
  float tmpvar_72;
  tmpvar_72 = (((
    (tmpvar_67 * tmpvar_71)
   - tmpvar_67) * tmpvar_67) + 1.0);
  float tmpvar_73;
  tmpvar_73 = (tmpvar_9 * tmpvar_9);
  float tmpvar_74;
  tmpvar_74 = pow ((1.0 - clamp (
    (tmpvar_66 + (tmpvar_66 * tmpvar_65))
  , 0.0, 1.0)), 5.0);
  final_color_1 = (((
    ((((
      (tmpvar_26.xyz - (tmpvar_26.xyz * vec3(tmpvar_10)))
     * 0.3183099) + (
      ((tmpvar_71 / ((3.141593 * tmpvar_72) * tmpvar_72)) * (0.5 / ((
        (tmpvar_68 * ((tmpvar_69 * (1.0 - tmpvar_73)) + tmpvar_73))
       + 
        (tmpvar_69 * ((tmpvar_68 * (1.0 - tmpvar_73)) + tmpvar_73))
      ) + 1e-05)))
     * 
      ((clamp ((50.0 * tmpvar_61.y), 0.0, 1.0) * tmpvar_74) + ((1.0 - tmpvar_74) * tmpvar_61))
    )) * tmpvar_68) * tmpvar_30[0])
   * tmpvar_29[0]) * tmpvar_32) + u_EmissiveColor);
  vec3 tmpvar_75;
  tmpvar_75 = mix (vec3(0.04, 0.04, 0.04), tmpvar_26.xyz, tmpvar_10);
  vec3 tmpvar_76;
  tmpvar_76 = (tmpvar_75 + ((vec3(1.0, 1.0, 1.0) - tmpvar_75) * pow (
    (1.0 - max (dot (tmpvar_25, tmpvar_7), 0.0))
  , 5.0)));
  final_color_1 = (final_color_1 + ((
    ((((vec3(1.0, 1.0, 1.0) - tmpvar_76) * (1.0 - tmpvar_10)) * tmpvar_46) * tmpvar_26.xyz)
   + 
    (texture2D (u_RadianceTex, radianceCoords_51).xyz * ((tmpvar_76 * tmpvar_59.x) + tmpvar_59.y))
  ) * max (vec3(tmpvar_12), 
    ((((
      (tmpvar_12 * ((2.0404 * tmpvar_26.xyz) - 0.3324))
     + 
      ((-4.7951 * tmpvar_26.xyz) + 0.6417)
    ) * tmpvar_12) + ((2.7552 * tmpvar_26.xyz) + 0.6903)) * tmpvar_12)
  )));
  vec4 tmpvar_77;
  tmpvar_77.xyz = final_color_1;
  tmpvar_77.w = tmpvar_11;
  vec4 tmpvar_78;
  vec4 color_79;
  color_79.w = tmpvar_77.w;
  color_79.x = clamp (final_color_1.x, 0.0, 1.0);
  color_79.yz = clamp (final_color_1.yz, vec2(0.0, 0.0), vec2(1.0, 1.0));
  tmpvar_78 = color_79;
  gl_FragColor = tmpvar_78;
  //#endif
  //gl_FragColor = vec4(texture2D(u_BaseColor, g_vary_Texcoord).rgb, 1.0); //vec4(1.0, 0.0, 0.0, 1.0);
}

