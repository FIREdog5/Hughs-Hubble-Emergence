package bin.graphics;

import bin.ClientMain;

import java.util.Stack;
import java.util.HashMap;
import java.util.HashSet;

import com.jogamp.opengl.GL2;

public class FramebufferController {

  private Stack<Integer> frameHistory;
  private int currentFramebuffer;
  private HashMap<Integer, Integer> texData;
  private HashMap<Integer, Integer> altTexData;
  private HashMap<Integer, Integer> rboData;
  private HashSet<Integer> deadFbos;

  public FramebufferController() {
    this.frameHistory = new Stack<Integer>();
    this.currentFramebuffer = 0;
    this.texData = new HashMap<Integer,Integer>();
    this.altTexData = new HashMap<Integer,Integer>();
    this.rboData = new HashMap<Integer,Integer>();
    this.deadFbos = new HashSet<Integer>();
  }

  public int createNewFrame() {
    GL2 gl = ClientMain.gl;

    int[] framebufferWrapper = new int[1];
    gl.glGenFramebuffers(1, framebufferWrapper, 0);
    int framebuffer = framebufferWrapper[0];
    //bind this new buffer for now
    gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, framebuffer);
    if (deadFbos.contains(framebuffer)) {
      deadFbos.remove(framebuffer);
    }

    int[] boundFbo = new int[1];
    gl.glGetIntegerv(GL2.GL_FRAMEBUFFER_BINDING, boundFbo, 0);
    if(boundFbo[0] != framebuffer) {
      System.out.println("ERROR::FRAMEBUFFER:: Framebuffer init failed");
    }


    //make a texture
    int[] textureColorbufferWrapper = new int[1];
    gl.glGenTextures(1, textureColorbufferWrapper, 0);
    int textureColorbuffer = textureColorbufferWrapper[0];
    gl.glBindTexture(GL2.GL_TEXTURE_2D, textureColorbuffer);
    gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, Renderer.getWindowWidth(), Renderer.getWindowHeight(), 0, GL2.GL_RGBA, GL2.GL_FLOAT, null);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);

    //make an alternate texture
    int[] alternateTextureColorbufferWrapper = new int[1];
    gl.glGenTextures(1, alternateTextureColorbufferWrapper, 0);
    int alternateTextureColorbuffer = alternateTextureColorbufferWrapper[0];
    gl.glBindTexture(GL2.GL_TEXTURE_2D, alternateTextureColorbuffer);
    gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, Renderer.getWindowWidth(), Renderer.getWindowHeight(), 0, GL2.GL_RGBA, GL2.GL_FLOAT, null);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);

    // attach it to currently bound framebuffer object
    gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL2.GL_TEXTURE_2D, textureColorbuffer, 0);
    this.texData.put(framebuffer, textureColorbuffer);
    this.altTexData.put(framebuffer, alternateTextureColorbuffer);

    //make an rbo
    int[] rboWrapper = new int[1];
    gl.glGenRenderbuffers(1, rboWrapper, 0);
    int rbo = rboWrapper[0];
    gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, rbo);
    gl.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_DEPTH24_STENCIL8, Renderer.getWindowWidth(), Renderer.getWindowHeight());
    gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, 0);

    // attach it to currently bound framebuffer object
    gl.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_STENCIL_ATTACHMENT, GL2.GL_RENDERBUFFER, rbo);
    this.rboData.put(framebuffer, rbo);

    //check if it worked and unbind the fbo
    if(gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER) != GL2.GL_FRAMEBUFFER_COMPLETE){
    	System.out.println("ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
    } else {
      System.out.println("created framebuffer: " + framebuffer);
    }
    gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, this.peakHistory());
    return framebuffer;
  }

  private int peakHistory() {
    if (frameHistory.size() > 0) {
      return frameHistory.peek();
    }
    return 0;
  }

  public void switchToFrame(int framebuffer, boolean transparent) {
    GL2 gl = ClientMain.gl;
    gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, framebuffer);
    gl.glViewport(0, 0, Renderer.getWindowWidth(), Renderer.getWindowHeight());
    if (transparent) {
      gl.glClearColor(0f, 0f, 0f, 0f);
    } else {
      gl.glClearColor(0f, 0f, 0f, 1.0f);
    }
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT | GL2.GL_STENCIL_BUFFER_BIT);
    gl.glClearColor(0f, 0f, 0f, 1.0f);
    this.frameHistory.push(currentFramebuffer);
    this.currentFramebuffer = framebuffer;
  }

  public void popFrameAndBind() {
    if(this.frameHistory.size() == 0) {
      return;
    }
    GL2 gl = ClientMain.gl;
    int oldBuffer = this.frameHistory.pop();
    if (deadFbos.contains(oldBuffer)) {
      popFrameAndBind();
      return;
    }

    int currTex = this.texData.get(this.currentFramebuffer);
    int altTex = this.altTexData.get(this.currentFramebuffer);
    this.texData.put(this.currentFramebuffer, altTex);
    this.altTexData.put(this.currentFramebuffer, currTex);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, currTex);
    gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL2.GL_TEXTURE_2D, altTex, 0);
    gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, oldBuffer);
    gl.glViewport(0, 0, Renderer.getWindowWidth(), Renderer.getWindowHeight());
    this.currentFramebuffer = oldBuffer;
  }

  public void cleanUpFrame(int framebuffer) {
    GL2 gl = ClientMain.gl;
    gl.glDeleteFramebuffers(1, new int[]{framebuffer}, 0);
    gl.glDeleteTextures(1, new int[]{texData.get(framebuffer)}, 0);
    gl.glDeleteRenderbuffers(1, new int[]{rboData.get(framebuffer)}, 0);
    deadFbos.add(framebuffer);
    texData.remove(framebuffer);
    altTexData.remove(framebuffer);
    rboData.remove(framebuffer);
  }
}
