/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.jwavefront;

import br.usp.icmc.vicg.gl.util.Shader;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLProfile;

/**
 *
 * @author PC
 */
public class JWavefrontObject {

  private int[] vao;
  private GL3 gl;
  private br.usp.icmc.vicg.gl.core.Material material;
  private int vertex_positions_handle;
  private int vertex_normals_handle;
  private int vertex_textures_handle;
  private int is_texture_handle;
  private int texture_handle;
  private ArrayList<Group> groups;
  private ArrayList<Vertex> vertices;
  private ArrayList<Normal> normals;
  private ArrayList<Material> materials;
  private ArrayList<Texture> textures;
  private ArrayList<TextureCoord> textures_coord;
  private File pathname;
  private Group current_group;

  /**
   * Construct a JWavefrontObject object.
   *
   * @param file The file containing the object.
   * @param shader
   * @throws IOException
   */
  public JWavefrontObject(File file) {
    groups = new ArrayList<Group>();
    vertices = new ArrayList<Vertex>() {
      @Override
      public Vertex get(int i) {
        return super.get(i - 1);
      }
    };
    normals = new ArrayList<Normal>() {
      @Override
      public Normal get(int i) {
        return super.get(i - 1);
      }
    };
    textures_coord = new ArrayList<TextureCoord>() {
      @Override
      public TextureCoord get(int i) {
        return super.get(i - 1);
      }
    };
    materials = new ArrayList<Material>();
    textures = new ArrayList<Texture>();

    pathname = file;
    current_group = null;
  }

  public void init(GL3 gl, Shader shader) throws IOException {
    this.gl = gl;

    this.material = new br.usp.icmc.vicg.gl.core.Material();
    this.material.init(gl, shader);

    this.vertex_positions_handle = shader.getAttribLocation("a_position");
    this.vertex_normals_handle = shader.getAttribLocation("a_normal");
    this.vertex_textures_handle = shader.getAttribLocation("a_texcoord");

    //control if it is a texture or material
    this.is_texture_handle = shader.getUniformLocation("u_is_texture");
    this.texture_handle = shader.getUniformLocation("u_texture");

    parse(pathname);
  }

  /**
   * "unitize" a model by translating it to the origin and scaling it to fit in
   * a unit cube around the origin.
   *
   * @return Returns the scalefactor used.
   */
  public void unitize() {
    assert (vertices != null);

    float maxx, minx, maxy, miny, maxz, minz;
    float cx, cy, cz, w, h, d;
    float scale;

    /*
     * get the max/mins
     */
    maxx = minx = vertices.get(1).x;
    maxy = miny = vertices.get(1).y;
    maxz = minz = vertices.get(1).z;

    for (int i = 1; i <= vertices.size(); i++) {
      if (maxx < vertices.get(i).x) {
        maxx = vertices.get(i).x;
      }
      if (minx > vertices.get(i).x) {
        minx = vertices.get(i).x;
      }

      if (maxy < vertices.get(i).y) {
        maxy = vertices.get(i).y;
      }
      if (miny > vertices.get(i).y) {
        miny = vertices.get(i).y;
      }

      if (maxz < vertices.get(i).z) {
        maxz = vertices.get(i).z;
      }
      if (minz > vertices.get(i).z) {
        minz = vertices.get(i).z;
      }
    }

    /*
     * calculate model width, height, and depth
     */
    w = Math.abs(maxx) + Math.abs(minx);
    h = Math.abs(maxy) + Math.abs(miny);
    d = Math.abs(maxz) + Math.abs(minz);

    /*
     * calculate center of the model
     */
    cx = (maxx + minx) / 2.0f;
    cy = (maxy + miny) / 2.0f;
    cz = (maxz + minz) / 2.0f;

    /*
     * calculate unitizing scale factor
     */
    scale = 2.0f / Math.max(Math.max(w, h), d);

    /*
     * translate around center then scale
     */
    for (int i = 1; i <= vertices.size(); i++) {
      vertices.get(i).x -= cx;
      vertices.get(i).y -= cy;
      vertices.get(i).z -= cz;
      vertices.get(i).x *= scale;
      vertices.get(i).y *= scale;
      vertices.get(i).z *= scale;
    }
  }

  /**
   * Reads a model description from a Wavefront.
   *
   * @param file The file containing the Wavefront model.
   * @throws IOException
   */
  private void parse(File file) throws IOException {
    BufferedReader in = null;
    StringTokenizer tok, tok2;
    String token;
    String line = null;
    int id = 0;

    try {
      in = new BufferedReader(new FileReader(file));

      while ((line = in.readLine()) != null) {
        line = line.trim();

        if (line.length() > 0) {
          switch (line.charAt(0)) {
            case '#': /* comment */

              break;
            case 'v': /* v, vn, vt */

              switch (line.charAt(1)) {
                case ' ': /* vertex */

                  tok = new StringTokenizer(line, " ");
                  tok.nextToken(); //ignores v
                  Vertex v = new Vertex(id++,
                          Float.parseFloat(tok.nextToken()),
                          Float.parseFloat(tok.nextToken()),
                          Float.parseFloat(tok.nextToken()));
                  vertices.add(v);
                  break;
                case 'n': /* normal */

                  tok = new StringTokenizer(line, " ");
                  tok.nextToken(); //ignores vn
                  Normal n = new Normal(Float.parseFloat(tok.nextToken()),
                          Float.parseFloat(tok.nextToken()),
                          Float.parseFloat(tok.nextToken()));
                  normals.add(n);
                  break;
                case 't': /* texcoord */

                  tok = new StringTokenizer(line, " ");
                  tok.nextToken(); //ignores vt
                  TextureCoord tc = new TextureCoord(Float.parseFloat(tok.nextToken()),
                          Float.parseFloat(tok.nextToken()));
                  textures_coord.add(tc);
                  break;
                default:
                  Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                          "parse() error: line not recognized >> " + line, file.getName());

              }
              break;
            case 'm': /* mtllib */

              tok = new StringTokenizer(line, " ");
              token = tok.nextToken(); //ignores mtllib

              if (token.equals("mtllib")) {
                token = tok.nextToken();
                parse_mtl(token);
              } else {
                Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                        "parse() error: line not recognized >> " + line, file.getName());
              }
              break;
            case 'u': /* usemtl */

              tok = new StringTokenizer(line, " ");
              token = tok.nextToken();

              if (token.equals("usemtl")) {
                token = tok.nextToken();

                if (current_group == null) {
                  current_group = findGroup(Group.default_group.name);

                  if (current_group == null) {
                    current_group = Group.default_group;
                    groups.add(current_group);
                  }
                }

                Material aux = findMaterial(token);

                if (current_group.material != aux
                        && current_group.material != Material.default_material) {
                  //when changing material inside a group, I
                  //I have to create a new group                                    
                  current_group = new Group("group_" + groups.size());
                  groups.add(current_group);
                }

                current_group.material = aux;
              } else {
                Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                        "parse() error: line not recognized >> " + line, file.getName());
              }
              break;
            case 'g':
            case 'o': /* group */

              tok = new StringTokenizer(line, " ");
              tok.nextToken(); //ignores g

              if (tok.hasMoreTokens()) {
                token = tok.nextToken();
                current_group = findGroup(token);

                if (current_group == null) {
                  current_group = new Group(token);
                  groups.add(current_group);
                }
              } else {
                current_group = findGroup(Group.default_group.name);

                if (current_group == null) {
                  current_group = Group.default_group;
                  groups.add(current_group);
                }
              }
              break;
            case 'f': /* face */

              //getting the current group
              if (current_group == null) {
                current_group = findGroup(Group.default_group.name);

                if (current_group == null) {
                  current_group = Group.default_group;
                  groups.add(current_group);
                }
              }

              boolean hastexture = (current_group.material.texture != null);

              line = line.trim().substring(1).trim(); //removing f

              if (line.contains("//")) { /* v//n */

                Triangle tri = new Triangle();
                tok = new StringTokenizer(line, " ");

                tok2 = new StringTokenizer(tok.nextToken(), "/");
                tri.vertices[0] = vertices.get(Integer.parseInt(tok2.nextToken()));
                tri.vertex_normals[0] = normals.get(Integer.parseInt(tok2.nextToken()));

                tok2 = new StringTokenizer(tok.nextToken(), "/");
                tri.vertices[1] = vertices.get(Integer.parseInt(tok2.nextToken()));
                tri.vertex_normals[1] = normals.get(Integer.parseInt(tok2.nextToken()));

                tok2 = new StringTokenizer(tok.nextToken(), "/");
                tri.vertices[2] = vertices.get(Integer.parseInt(tok2.nextToken()));
                tri.vertex_normals[2] = normals.get(Integer.parseInt(tok2.nextToken()));

                current_group.triangles.add(tri);

                while (tok.hasMoreTokens()) {
                  Triangle new_tri = new Triangle();
                  tok2 = new StringTokenizer(tok.nextToken(), "/");

                  new_tri.vertices[0] = tri.vertices[0];
                  new_tri.vertices[1] = tri.vertices[2];
                  new_tri.vertices[2] = vertices.get(Integer.parseInt(tok2.nextToken()));

                  new_tri.vertex_normals[0] = tri.vertex_normals[0];
                  new_tri.vertex_normals[1] = tri.vertex_normals[2];
                  new_tri.vertex_normals[2] = normals.get(Integer.parseInt(tok2.nextToken()));

                  current_group.triangles.add(new_tri);
                  tri = new_tri;
                }
              } else {
                Triangle tri = new Triangle();
                tok = new StringTokenizer(line, " ");
                tok2 = new StringTokenizer(tok.nextToken(), "/");

                if (tok2.countTokens() == 3) { /* v/t/n */

                  tri.vertices[0] = vertices.get(Integer.parseInt(tok2.nextToken()));
                  if (hastexture) {
                    tri.vertex_tex_coords[0] = textures_coord.get(Integer.parseInt(tok2.nextToken()));
                  }
                  tri.vertex_normals[0] = normals.get(Integer.parseInt(tok2.nextToken()));

                  tok2 = new StringTokenizer(tok.nextToken(), "/");
                  tri.vertices[1] = vertices.get(Integer.parseInt(tok2.nextToken()));
                  if (hastexture) {
                    tri.vertex_tex_coords[1] = textures_coord.get(Integer.parseInt(tok2.nextToken()));
                  }
                  tri.vertex_normals[1] = normals.get(Integer.parseInt(tok2.nextToken()));

                  tok2 = new StringTokenizer(tok.nextToken(), "/");
                  tri.vertices[2] = vertices.get(Integer.parseInt(tok2.nextToken()));
                  if (hastexture) {
                    tri.vertex_tex_coords[2] = textures_coord.get(Integer.parseInt(tok2.nextToken()));
                  }
                  tri.vertex_normals[2] = normals.get(Integer.parseInt(tok2.nextToken()));

                  current_group.triangles.add(tri);

                  while (tok.hasMoreTokens()) {
                    Triangle new_tri = new Triangle();
                    tok2 = new StringTokenizer(tok.nextToken(), "/");

                    new_tri.vertices[0] = tri.vertices[0];
                    new_tri.vertices[1] = tri.vertices[2];
                    new_tri.vertices[2] = vertices.get(Integer.parseInt(tok2.nextToken()));

                    if (hastexture) {
                      new_tri.vertex_tex_coords[0] = tri.vertex_tex_coords[0];
                      new_tri.vertex_tex_coords[1] = tri.vertex_tex_coords[2];
                      new_tri.vertex_tex_coords[2] = textures_coord.get(Integer.parseInt(tok2.nextToken()));
                    }

                    new_tri.vertex_normals[0] = tri.vertex_normals[0];
                    new_tri.vertex_normals[1] = tri.vertex_normals[2];
                    new_tri.vertex_normals[2] = normals.get(Integer.parseInt(tok2.nextToken()));

                    current_group.triangles.add(new_tri);
                    tri = new_tri;
                  }
                } else if (tok2.countTokens() == 2) {  /* v/t */

                  tri.vertices[0] = vertices.get(Integer.parseInt(tok2.nextToken()));
                  if (hastexture) {
                    tri.vertex_tex_coords[0] = textures_coord.get(Integer.parseInt(tok2.nextToken()));
                  }

                  tok2 = new StringTokenizer(tok.nextToken(), "/");
                  tri.vertices[1] = vertices.get(Integer.parseInt(tok2.nextToken()));
                  if (hastexture) {
                    tri.vertex_tex_coords[1] = textures_coord.get(Integer.parseInt(tok2.nextToken()));
                  }

                  tok2 = new StringTokenizer(tok.nextToken(), "/");
                  tri.vertices[2] = vertices.get(Integer.parseInt(tok2.nextToken()));
                  if (hastexture) {
                    tri.vertex_tex_coords[2] = textures_coord.get(Integer.parseInt(tok2.nextToken()));
                  }

                  current_group.triangles.add(tri);

                  while (tok.hasMoreTokens()) {
                    Triangle new_tri = new Triangle();
                    tok2 = new StringTokenizer(tok.nextToken(), "/");

                    new_tri.vertices[0] = tri.vertices[0];
                    new_tri.vertices[1] = tri.vertices[2];
                    new_tri.vertices[2] = vertices.get(Integer.parseInt(tok2.nextToken()));

                    if (hastexture) {
                      new_tri.vertex_tex_coords[0] = tri.vertex_tex_coords[0];
                      new_tri.vertex_tex_coords[1] = tri.vertex_tex_coords[2];
                      new_tri.vertex_tex_coords[2] = textures_coord.get(Integer.parseInt(tok2.nextToken()));
                    }

                    current_group.triangles.add(new_tri);
                    tri = new_tri;
                  }
                } else {/* v */

                  tok = new StringTokenizer(line, " ");
                  tri.vertices[0] = vertices.get(Integer.parseInt(tok.nextToken()));
                  tri.vertices[1] = vertices.get(Integer.parseInt(tok.nextToken()));

                  if (tok.hasMoreTokens()) {
                    tri.vertices[2] = vertices.get(Integer.parseInt(tok.nextToken()));
                  } else {
                    tri.vertices[2] = tri.vertices[0];
                  }

                  current_group.triangles.add(tri);

                  while (tok.hasMoreTokens()) {
                    Triangle new_tri = new Triangle();

                    new_tri.vertices[0] = tri.vertices[0];
                    new_tri.vertices[1] = tri.vertices[2];
                    new_tri.vertices[2] = vertices.get(Integer.parseInt(tok.nextToken()));

                    current_group.triangles.add(new_tri);
                    tri = new_tri;
                  }
                }
              }
              break;
            case 's':
              //ignored
              break;
            default:
              Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                      "parse() error: line not recognized >> " + line, file.getName());
              break;
          }
        }
      }
    } catch (java.util.NoSuchElementException e) {
      System.out.println(line);
      throw new IOException(e);
    } catch (FileNotFoundException ex) {
      throw new IOException(ex.getMessage());
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException ex) {
          Logger.getLogger(JWavefrontObject.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }

    //calculate the face normals for the triangles of each group
    for (int i = 0; i < groups.size(); i++) {
      groups.get(i).calculate_face_normals();
    }

    //if the vertex normals are not presented, calculate them
    if (normals.isEmpty()) {
      calculate_vertex_normals();
    }
  }

  /**
   * Find a group in the model.
   *
   * @param name Group name.
   * @return Return the first group with the given name.
   */
  private Group findGroup(String name) {
    for (int i = 0; i < groups.size(); i++) {
      if (groups.get(i).name.toLowerCase().equals(name.toLowerCase())) {
        return groups.get(i);
      }
    }
    return null;
  }

  /**
   * Find a material in the model.
   *
   * @param name Material name.
   * @return Return the first material with the given name.
   */
  private Material findMaterial(String name) {
    /*
     * XXX doing a linear search on a string key'd list is pretty lame, but
     * it works and is fast enough for now.
     */
    for (int i = 0; i < materials.size(); i++) {
      if (materials.get(i).name.toLowerCase().equals(name.toLowerCase())) {
        return materials.get(i);
      }
    }

    return Material.default_material;
  }

  /**
   * Find a texture in the model.
   *
   * @param name Texture name.
   * @return Return the first texture with the given name.
   */
  private Texture findTexture(String name) throws IOException {
    /*
     * XXX doing a linear search on a string key'd list is pretty lame, but
     * it works and is fast enough for now.
     */
    for (int i = 0; i < textures.size(); i++) {
      if (textures.get(i).name.toLowerCase().equals(name.toLowerCase())) {
        return textures.get(i);
      }
    }

    return null;
  }

  /**
   * Read a wavefront material library file.
   *
   * @param name The filename of the material file
   * @throws IOException
   */
  private void parse_mtl(String name) throws IOException {
    File file = new File(pathname.getParent() + "/" + name);

    if (file.exists()) {
      BufferedReader in = null;
      StringTokenizer tok;

      try {
        in = new BufferedReader(new FileReader(file));
        Material material = null;

        String line;
        while ((line = in.readLine()) != null) {
          line = line.trim();

          if (line.length() > 0) {
            switch (line.charAt(0)) {
              case '#': /* comment */

                break;
              case 'n': /* newmtl */

                tok = new StringTokenizer(line, " ");
                String token = tok.nextToken(); //ignores newmtl

                //creating the new material
                if (token.equals("newmtl")) {
                  material = new Material(tok.nextToken());
                  materials.add(material);
                } else {
                  Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                          "parse_mtl() error: line not recognized >> " + line, file.getName());
                }

                break;
              case 'N': /* Ni or Ns */

                switch (line.charAt(1)) {
                  case 'i': /* ignored */

                    break;
                  case 's':
                    tok = new StringTokenizer(line, " ");
                    tok.nextToken(); //ignores Ns
                    material.shininess = Float.parseFloat(tok.nextToken());

                    /*
                     * wavefront shininess is from [0,
                     * 1000], so scale for OpenGL
                     */
                    material.shininess = (material.shininess / 1000.0f) * 128.0f;
                    break;
                  default:
                    Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                            "parse_mtl() error: line not recognized >> " + line, file.getName());
                }
                break;

              case 'K': /* Kd, Ks, or Ka */

                switch (line.charAt(1)) {
                  case 'd':
                    tok = new StringTokenizer(line, " ");
                    tok.nextToken(); //ignores Kd
                    material.diffuse[0] = Float.parseFloat(tok.nextToken());
                    material.diffuse[1] = Float.parseFloat(tok.nextToken());
                    material.diffuse[2] = Float.parseFloat(tok.nextToken());
                    break;
                  case 's':
                    tok = new StringTokenizer(line, " ");
                    tok.nextToken(); //ignores Ks
                    material.specular[0] = Float.parseFloat(tok.nextToken());
                    material.specular[1] = Float.parseFloat(tok.nextToken());
                    material.specular[2] = Float.parseFloat(tok.nextToken());
                    break;
                  case 'a':
                    tok = new StringTokenizer(line, " ");
                    tok.nextToken(); //ignores Ka
                    material.ambient[0] = Float.parseFloat(tok.nextToken());
                    material.ambient[1] = Float.parseFloat(tok.nextToken());
                    material.ambient[2] = Float.parseFloat(tok.nextToken());
                    break;
                  default:
                    Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                            "parse_mtl() error: line not recognized >> " + line, file.getName());
                    break;
                }
                break;
              case 'm': /* map_Kd */

                tok = new StringTokenizer(line, " ");
                token = tok.nextToken(); //ignores map_Kd

                if (token.equals("map_Kd")) {
                  name = tok.nextToken();

                  //loading the texture data
                  Texture texture = findTexture(name);
                  if (texture == null) {
                    file = new File(pathname.getParent() + "/" + name);

                    if (file.exists()) {
                      BufferedImage image = ImageIO.read(file);
                      ImageUtil.flipImageVertically(image); //vertically flip the image

                      texture = new Texture(name);
                      texture.texturedata = AWTTextureIO.newTexture(GLProfile.get(GLProfile.GL3), image, true);
                      texture.texturedata.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                      texture.texturedata.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                      textures.add(texture);
                    } else {
                      Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                              "parse_mtl() error: texture file not found " + name, file.getName());
                    }
                  }

                  material.texture = texture;
                } else {
                  Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                          "parse_mtl() error: line not recognized >> " + line, file.getName());
                }

                break;
              default:
                Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                        "parse_mtl() error: line not recognized >> " + line, file.getName());
                break;
            }
          }
        }
      } catch (FileNotFoundException ex) {
        throw new IOException(ex.getMessage());
      } finally {
        if (in != null) {
          try {
            in.close();
          } catch (IOException ex) {
            Logger.getLogger(JWavefrontObject.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      }
    } else {
      Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
              "readMTL() warning: mtl file not found ({0})", file.getName());
    }
  }

  /**
   * Renders the model to the current OpenGL context using the mode specified.
   *
   * @param gLAutoDrawable The OpenGL context to draw.
   * @param mode a bitwise OR of values describing what is to be rendered.
   * WF_NONE - write only vertices WF_FLAT - write facet normals WF_SMOOTH -
   * write vertex normals WF_TEXTURE - write texture coords WF_FLAT and
   * WF_SMOOTH should not both be specified.
   */
  public void draw() {
    if (vao == null) {
      create_vao();
    } else {
      for (int i = 0; i < groups.size(); i++) {
        Group group = groups.get(i);
        if (group.triangles.isEmpty()) {
          continue;
        }

        if (group.material.texture != null) {
          gl.glUniform1i(is_texture_handle, 1);
          gl.glUniform1i(texture_handle, 0);
          gl.glActiveTexture(GL3.GL_TEXTURE0);
          group.material.texture.texturedata.bind(gl);
        } else {
          gl.glUniform1i(is_texture_handle, 0);
        }

        if (group.material != null) {
          material.setAmbientColor(group.material.ambient);
          material.setDiffuseColor(group.material.diffuse);
          material.setSpecularColor(group.material.specular);
          material.setSpecularExponent(group.material.shininess);
          material.bind();
        }

        gl.glBindVertexArray(vao[i]);
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, 3 * group.triangles.size());
      }

      gl.glBindVertexArray(0);
    }
  }

  public void dispose() {
    gl.glDeleteVertexArrays(vao.length, vao, 0);
    gl.glBindVertexArray(0);
  }

  private void create_vao() {
    /* create one vao per group of vertex */
    vao = new int[groups.size()];

    gl.glGenVertexArrays(vao.length, vao, 0);

    //if the normals are not given per vertex, calculate it
    if (normals.isEmpty()) {
      calculate_vertex_normals();
    }

    for (int i = 0; i < groups.size(); i++) {
      Group group = groups.get(i);
      if (group.triangles.isEmpty()) {
        continue;
      }

      gl.glBindVertexArray(vao[i]); // Bind our Vertex Array Object so we can use it            

      float[] vertex_buffer = new float[9 * group.triangles.size()];
      float[] normal_buffer = new float[9 * group.triangles.size()];
      float[] texture_buffer = new float[6 * group.triangles.size()];

      for (int j = 0; j < group.triangles.size(); j++) {
        Triangle triangle = group.triangles.get(j);

        for (int k = 0; k < 3; k++) {
          vertex_buffer[(9 * j) + (3 * k)] = triangle.vertices[k].x;
          vertex_buffer[(9 * j) + (3 * k) + 1] = triangle.vertices[k].y;
          vertex_buffer[(9 * j) + (3 * k) + 2] = triangle.vertices[k].z;

          normal_buffer[(9 * j) + (3 * k)] = triangle.vertex_normals[k].x;
          normal_buffer[(9 * j) + (3 * k) + 1] = triangle.vertex_normals[k].y;
          normal_buffer[(9 * j) + (3 * k) + 2] = triangle.vertex_normals[k].z;

          if (triangle.vertex_tex_coords[k] != null) {
            texture_buffer[(6 * j) + (2 * k)] = triangle.vertex_tex_coords[k].u;
            texture_buffer[(6 * j) + (2 * k) + 1] = triangle.vertex_tex_coords[k].v;
          }
        }
      }

      int[] vbo = new int[3];
      gl.glGenBuffers(3, vbo, 0); // Generate two Vertex Buffer Object

      //the positions buffer
      gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0]); // Bind vertex buffer 
      gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertex_buffer.length * Buffers.SIZEOF_FLOAT,
              Buffers.newDirectFloatBuffer(vertex_buffer), GL3.GL_STATIC_DRAW);
      gl.glEnableVertexAttribArray(vertex_positions_handle);
      gl.glVertexAttribPointer(vertex_positions_handle, 3, GL3.GL_FLOAT, false, 0, 0);
      gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);

      //the normals buffer
      gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[1]); // Bind normals buffer
      gl.glBufferData(GL3.GL_ARRAY_BUFFER, normal_buffer.length * Buffers.SIZEOF_FLOAT,
              Buffers.newDirectFloatBuffer(normal_buffer), GL3.GL_STATIC_DRAW);
      gl.glEnableVertexAttribArray(vertex_normals_handle);
      gl.glVertexAttribPointer(vertex_normals_handle, 3, GL3.GL_FLOAT, false, 0, 0);
      gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);

      //the texture positions buffer
      if (group.material.texture != null) {
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[2]); // Bind normals buffer
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, texture_buffer.length * Buffers.SIZEOF_FLOAT,
                Buffers.newDirectFloatBuffer(texture_buffer), GL3.GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(vertex_textures_handle);
        gl.glVertexAttribPointer(vertex_textures_handle, 2, GL3.GL_FLOAT, false, 0, 0);
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
      }
    }

    gl.glBindVertexArray(0); // Disable our Vertex Buffer Object 
  }

  private void calculate_vertex_normals() {
    float angle = 90;
    Normal[] vertex_normals = new Normal[vertices.size()];

    for (int i = 0; i < groups.size(); i++) {
      Group group = groups.get(i);

      for (int j = 0; j < group.triangles.size(); j++) {
        Triangle triangle = group.triangles.get(j);

        int vertex_index_0 = triangle.vertices[0].id;
        if (vertex_normals[vertex_index_0] == null) {
          vertex_normals[vertex_index_0] = new Normal(triangle.face_normal.x,
                  triangle.face_normal.y, triangle.face_normal.z);
        } else {
          if (should_update_normal(vertex_normals[vertex_index_0],
                  triangle.face_normal, angle)) {
            vertex_normals[vertex_index_0].x += triangle.face_normal.x;
            vertex_normals[vertex_index_0].y += triangle.face_normal.y;
            vertex_normals[vertex_index_0].z += triangle.face_normal.z;
          }
        }

        int vertex_index_1 = triangle.vertices[1].id;
        if (vertex_normals[vertex_index_1] == null) {
          vertex_normals[vertex_index_1] = new Normal(triangle.face_normal.x,
                  triangle.face_normal.y, triangle.face_normal.z);
        } else {
          if (should_update_normal(vertex_normals[vertex_index_1],
                  triangle.face_normal, angle)) {
            vertex_normals[vertex_index_1].x += triangle.face_normal.x;
            vertex_normals[vertex_index_1].y += triangle.face_normal.y;
            vertex_normals[vertex_index_1].z += triangle.face_normal.z;
          }
        }

        int vertex_index_2 = triangle.vertices[2].id;
        if (vertex_normals[vertex_index_2] == null) {
          vertex_normals[vertex_index_2] = new Normal(triangle.face_normal.x,
                  triangle.face_normal.y, triangle.face_normal.z);
        } else {
          if (should_update_normal(vertex_normals[vertex_index_2],
                  triangle.face_normal, angle)) {
            vertex_normals[vertex_index_2].x += triangle.face_normal.x;
            vertex_normals[vertex_index_2].y += triangle.face_normal.y;
            vertex_normals[vertex_index_2].z += triangle.face_normal.z;
          }
        }
      }
    }

    for (int i = 0; i < vertex_normals.length; i++) {
      float norm = (float) Math.sqrt(vertex_normals[i].x * vertex_normals[i].x
              + vertex_normals[i].y * vertex_normals[i].y
              + vertex_normals[i].z * vertex_normals[i].z);

      if (norm > 0) {
        vertex_normals[i].x /= norm;
        vertex_normals[i].y /= norm;
        vertex_normals[i].z /= norm;
      }

      normals.add(vertex_normals[i]);
    }

    for (int i = 0; i < groups.size(); i++) {
      Group group = groups.get(i);

      for (int j = 0; j < group.triangles.size(); j++) {
        Triangle triangle = group.triangles.get(j);

        triangle.vertex_normals[0] = vertex_normals[triangle.vertices[0].id];
        triangle.vertex_normals[1] = vertex_normals[triangle.vertices[1].id];
        triangle.vertex_normals[2] = vertex_normals[triangle.vertices[2].id];
      }
    }
  }

  private boolean should_update_normal(Normal a, Normal b, float angle) {
    float cos_angle = (float) Math.cos(Math.toRadians(angle));
    float dot = a.x * b.x + a.y * b.y + a.z * b.z;
    return (dot > cos_angle);
  }

  public void dump() {
    for (int i = 0; i < groups.size(); i++) {
      System.out.println("----");
      groups.get(i).dump();
    }
  }
}
