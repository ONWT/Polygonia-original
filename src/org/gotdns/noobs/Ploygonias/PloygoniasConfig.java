package org.gotdns.noobs.Ploygonias;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.bukkit.util.config.Configuration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class PloygoniasConfig extends Configuration
{
  private static final Yaml yaml;
  private File file;
  public int mapRadius = 0;
  public boolean defaultEnter;
  public boolean defaultBuild;
  public boolean defaultDestroy;
  public boolean enableRadius;
  public int zoneTool = 280;

  static
  {
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    yaml = new Yaml(options);
  }

  public PloygoniasConfig(File file)
  {
    super(file);

    this.file = file;

    if (file == null)
      throw new IllegalArgumentException("file cannot be null");
  }

  public void setDefaults()
  {
    this.mapRadius = 1000;
    this.defaultEnter = true;
    this.defaultBuild = true;
    this.defaultDestroy = true;
    this.enableRadius = true;
    this.zoneTool = 280;
  }

  public void load()
  {
    setDefaults();

    if (this.file == null) {
      throw new IllegalArgumentException("file cannot be null");
    }

    if (!this.file.exists()) {
      try {
        this.file.createNewFile();
        save();
      }
      catch (IOException localIOException)
      {
      }
    }
    else
    {
      super.load();

      this.mapRadius = getInt("mapRadius", this.mapRadius);
      this.defaultEnter = getBoolean("defaultEnter", true);
      this.defaultBuild = getBoolean("defaultBuild", true);
      this.defaultDestroy = getBoolean("defaultDestroy", true);
      this.enableRadius = getBoolean("enableRadius", true);
      this.zoneTool = getInt("zoneTool", this.zoneTool);
    }
  }

  public boolean save()
  {
    this.root.put("mapRadius", Integer.valueOf(this.mapRadius));
    this.root.put("defaultEnter", Boolean.valueOf(this.defaultEnter));
    this.root.put("defaultBuild", Boolean.valueOf(this.defaultBuild));
    this.root.put("defaultDestroy", Boolean.valueOf(this.defaultDestroy));
    this.root.put("enableRadius", Boolean.valueOf(this.enableRadius));
    this.root.put("zoneTool", Integer.valueOf(this.zoneTool));
    try
    {
      FileOutputStream stream = new FileOutputStream(this.file);
      stream.getChannel().truncate(0L);
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
      try
      {
        writer.write(yaml.dump(this.root));
      }
      finally
      {
        writer.close();
      }

    }
    catch (IOException e)
    {
      return false;
    }
    return true;
  }
}