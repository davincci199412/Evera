# Block for declaring the script parameters.
Param(
    $createShareFolder = "",
    $appDataDir = "",
    $currentUserName = ""
)
if (Get-Command "AI_GetMsiProperty" -errorAction SilentlyContinue)
{
    $createShareFolder = (AI_GetMsiProperty TempFolder)
    $appDataDir = (AI_GetMsiProperty LocalAppDataFolder)
    $currentUserName = (AI_GetMsiProperty LogonUser)
}

$ErrorActionPreference = "Stop"

# Your code goes here.
$everaUserName = "evera-docker"
"everaUserName: " + $everaUserName

$currentEveraUser = Get-LocalUser | ?{$_.Name -eq $everaUserName}
"currentEveraUser: " + $currentEveraUser
if( ! $currentEveraUser )
{
    "Creating local user"
    $securePassword = ConvertTo-SecureString $everaUserName -AsPlainText -Force
    New-LocalUser -Name $everaUserName -Password $securePassword -Description "Account to use docker with evera." -AccountNeverExpires -PasswordNeverExpires
    "Local user created"
}
# TODO: set execution policy here?

"createShareFolder: " + $createShareFolder
"appDataDir: " + $appDataDir

$createShareScript = $createShareFolder + "create-share.ps1"
"createShareScript: " + $createShareScript

$everaDataDir = $appDataDir + "\evera\evera\default"
$mainnetDir = $everaDataDir + "\mainnet\ComputerRes"
"mainnetDir: " + $mainnetDir
$testnetDir = $everaDataDir + "\rinkeby\ComputerRes"
"testnetDir: " + $testnetDir

function EnsureShare {
    Param([string]$folder)
    "Ensure Shared folder"
    md $folder -Force
    "Folder created, create share"
    &"$createShareScript" "$everaUserName" "$folder"
    "Share created"
}

EnsureShare $mainnetDir
EnsureShare $testnetDir

"Add current user to the Hyper-V Administrators group"
# Create "Hyper-V Administrators" group

$HvAdminGroupSID = "S-1-5-32-578"
$HvAdminGroup =(gwmi Win32_Group | ?{$_.sid -eq $HvAdminGroupSID})
"Found group?"
"Admin group: " + $HvAdminGroup
if( $HvAdminGroup )
{
    "currentUserName:" + $currentUserName
    $fullUserName = "$env:computername\$currentUserName"
    $isMember = (Get-LocalGroupMember -sid $HvAdminGroup.sid  | ?{$_.name -eq $fullUserName})
    "Is the current user member?"
    "isMember: " + $isMember
    if ( ! $isMember )
    {
        "Add current user to Hyper-V Administrators group"
        Add-LocalGroupMember -sid $HvAdminGroup.sid -member $fullUserName

        $isMember = (Get-LocalGroupMember -sid $HvAdminGroup.sid  | ?{$_.name -eq $fullUserName})
        "Is the current user member?"
      "isMember: " + $isMember
        if ( ! $isMember )
        {
                "Installer failed to add current user to hyperv group"
                exit 1
        }
    }
}

"Check Evera SMB firewall rule"
$firewallRule = Get-NetFirewallRule | ?{$_.name -eq "EVERA-SMB"}
"Current rule: " + $firewallRule
if( ! $firewallRule )
{
    New-NetFirewallRule -DisplayName "Evera SMB" -Name "EVERA-SMB" `
     -Direction Inbound -LocalPort 445 -Protocol TCP `
     -RemoteAddress 172.16.0.0/12 -LocalAddress 172.16.0.0/12 `
     -Program System -Action Allow

    $firewallRule = Get-NetFirewallRule | ?{$_.name -eq "EVERA-SMB"}
    "Created rule: " + $firewallRule
    if( ! $firewallRule )
    {
        "Failed to create firewall rule."
        exit 1
    }
}

exit 0
