# Installs (first run) and starts a local Jenkins for the McD-LocationModule pipeline.
# Usage (PowerShell):  .\jenkins\start-jenkins.ps1
# Then open http://localhost:8090  (user: admin, password in $JenkinsDir\home\secrets\admin-password.txt)

param(
    [string]$JenkinsDir = "$env:USERPROFILE\Jenkins",
    [string]$JavaHome = "C:\Program Files\Java\jdk-21.0.12.1",
    [int]$Port = 8090
)

$ErrorActionPreference = 'Stop'
$java = Join-Path $JavaHome 'bin\java.exe'
$home_ = Join-Path $JenkinsDir 'home'
$war = Join-Path $JenkinsDir 'jenkins.war'
$pluginManager = Join-Path $JenkinsDir 'jenkins-plugin-manager.jar'

New-Item -ItemType Directory -Force -Path $JenkinsDir, $home_, "$home_\init.groovy.d" | Out-Null

if (-not (Test-Path $war)) {
    Write-Host 'Downloading Jenkins LTS...'
    Invoke-WebRequest 'https://get.jenkins.io/war-stable/latest/jenkins.war' -OutFile $war
}
if (-not (Test-Path $pluginManager)) {
    Write-Host 'Downloading Jenkins plugin installation manager...'
    $release = Invoke-RestMethod 'https://api.github.com/repos/jenkinsci/plugin-installation-manager-tool/releases/latest'
    $asset = $release.assets | Where-Object { $_.name -like '*.jar' } | Select-Object -First 1
    Invoke-WebRequest $asset.browser_download_url -OutFile $pluginManager
}

# Plugins (Pipeline, Git, JUnit, Timestamper)
& $java -jar $pluginManager --war $war --plugin-download-directory "$home_\plugins" --plugin-file "$PSScriptRoot\plugins.txt"

# Start-up scripts: admin user + pipeline job
Copy-Item "$PSScriptRoot\init.groovy.d\*.groovy" "$home_\init.groovy.d\" -Force

$env:JENKINS_HOME = $home_
Write-Host "Starting Jenkins on http://localhost:$Port ..."
& $java "-Djenkins.install.runSetupWizard=false" -jar $war "--httpPort=$Port" "--httpListenAddress=127.0.0.1"
