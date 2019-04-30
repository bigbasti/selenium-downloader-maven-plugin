package com.bigbasti;

import com.bigbasti.model.FileDownload;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Mojo(name = "selenium", defaultPhase = LifecyclePhase.TEST_COMPILE)
public class SeleniumDownloaderMojo extends AbstractMojo
{

    @Component
    protected MojoExecution execution;

    @Parameter(defaultValue = "${basedir}", property = "workingDirectory", required = false)
    protected File workingDirectory;

    /**
     * the directory to which the files should be downloaded before extracting
     */
    @Parameter(defaultValue = "${project.basedir}/src/test/resources/webdriver/zipped")
    protected File downloadTargetDirectory;

    /**
     * the root directory to which the downloaded files will be extracted
     */
    @Parameter(defaultValue = "${project.basedir}/src/test/resources/webdriver/zipped")
    protected File driverTargetDirectory;

    /**
     * Download drivers for the OS the plugin is currently is executed on and skip all others.
     * If set to false, ALL registered downloads will be executed
     */
    @Parameter(defaultValue = "true")
    protected boolean downloadOnlyCurrentOs;

    /**
     * list of all files which should be downloaded. Each file must be registered separately.
     * Example:
     * <downloads>
     *     <fileDownload>
     *         <os>windows</os>
     *         <driver>chrome</driver>
     *         <version>2.46</version>
     *         <architecture>x32</architecture>
     *         <url>https://chromedriver.storage.googleapis.com/2.46/chromedriver_win32.zip</url>
     *         <hash>a95696782d22d94bb60f83a315e8a584f2ff5db2</hash> [optional]
     *         <algorithm>sha1</algorithm> [optional]
     *     </fileDownload>
     *     <fileDownload>
     *         <os>linux</os>
     *         <driver>chrome</chrome>
     *         <version>2.46</version>
     *         <architecture>x64</architecture>
     *         <url>https://chromedriver.storage.googleapis.com/2.46/chromedriver_linux64.zip</url>
     *         <hash>402a44dc7719f7d40d4493942e2bd3238fa99ce5</hash> [optional]
     *         <algorithm>sha1</algorithm> [optional]
     *     </fileDownload>
     * </downloads>
     * NOTE: provide the hash property only if you want the checksum to be checked. If you do not provide
     * this property no checksum will be executed. If you provide the hash property you also must
     * provide the algorithm property to specify which algorithm to use for checksum calculation
     */
    @Parameter
    protected List<FileDownload> downloads;

    /**
     * specify which proxy to use for the download of files.
     * - default -> no specific proxy, use system default configuration
     * - system -> check whether there is a http_proxy or https_proxy env variable and use its value.
     *             if there is no en variable set this setting acts like default
     * - maven -> if maven has a proxy configured (in its settings.xml) use it
     *            when there is no proxy setting in maven this setting acts like default
     */
    @Parameter(defaultValue = "maven")
    protected String useProxy;

    /**
     * if true the file will be downloaded on every run and replace the old one.
     * if set to false, the download will be skipped when there is already a file with the matching name
     */
    @Parameter(defaultValue = "false")
    protected boolean overwriteExisting;

    /**
     * how often should the download be retried on failure
     */
    @Parameter(defaultValue = "1")
    protected int downloadRetryAttempts;

    /**
     * how log to wait for the download to start or the server to answer in msec
     */
    @Parameter(defaultValue = "10000")
    protected int downloadConnectTimeout;

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
