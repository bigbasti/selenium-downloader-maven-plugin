package com.bigbasti;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Mojo(name = "selenium", defaultPhase = LifecyclePhase.TEST_COMPILE)
public class MyMojo extends AbstractMojo
{
    /*
        <configuration>
            <useProxy>default|system|maven</useProxy> [default: maven][optional]
            <!-- if there is more than one os specified in the downloads section, only download file for the current os -->
            <!-- this speeds up the build process as fewer files must be downloaded -->
            <downloadOnlyCurrentOs>true</downloadOnlyCurrentOs> [default:false][optional]
            <!-- if there is already a file with a matching name at the target location skip downloading -->
            <overwriteExisting>false</overwriteExisting> [default: false][optional]
            <!-- the directory to save the downloaded zip files containing the drivers -->
            <downloadTargetDirectory>${project.basedir}/src/test/resources/webdriver/zipped</downloadTargetDirectory> [default: ${project.basedir}/src/test/resources/webdriver/zipped][optional]
            <!-- the directory where the unzipped driver binaries should be copied to -->
            <driverTargetDirectory>${project.basedir}/src/test/resources/webdriver/binaries</driverTargetDirectory>[default: ${project.basedir}/src/test/resources/webdriver/binaries][optional]
            <downloadRetryAttempts>1</downloadRetryAttempts> [default: 1][optional]
            <downloadConnectTimeout>10000</downloadConnectTimeout> [defaulr: 10000][optional]
            <downloads>
                <download>
                    <os>windows</os>
                    <driver>chrome</chrome>
                    <version>2.46</version>
                    <architecture>64</architecture>
                    <url>https://chromedriver.storage.googleapis.com/2.46/chromedriver_win32.zip</url>
                    <hash>a95696782d22d94bb60f83a315e8a584f2ff5db2</hash> [optional]
                    <algorithm>sha1</algorithm> [optional]
                </download>
                <download>
                    <os>linux</os>
                    <driver>chrome</chrome>
                    <version>2.46</version>
                    <architecture>64</architecture>
                    <url>https://chromedriver.storage.googleapis.com/2.46/chromedriver_linux64.zip</url>
                    <hash>402a44dc7719f7d40d4493942e2bd3238fa99ce5</hash> [optional]
                    <algorithm>sha1</algorithm> [optional]
                </download>
            </downloads>
        </configuration>
     */


    /**
     * Location of the file.
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    public void execute()
        throws MojoExecutionException
    {
        File f = outputDirectory;

        if ( !f.exists() )
        {
            f.mkdirs();
        }

        File touch = new File( f, "touch.txt" );

        FileWriter w = null;
        try
        {
            w = new FileWriter( touch );

            w.write( "touch.txt" );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error creating file " + touch, e );
        }
        finally
        {
            if ( w != null )
            {
                try
                {
                    w.close();
                }
                catch ( IOException e )
                {
                    // ignore
                }
            }
        }
    }
}
