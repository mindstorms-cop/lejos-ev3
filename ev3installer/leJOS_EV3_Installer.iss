#preproc ispp
#if VER < EncodeVer(5,4,3)
  #error Use Inno Setup 5.4.3(unicode) or newer
#endif
#ifndef UNICODE
  #error Use the unicode build of Inno Setup
#endif

#ifndef MyAppVersion
  #define MyAppVersion "0.8.1-beta"
#endif

; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
#define MyAppID "8A65DB51-0626-406E-869A-BAAC2381C494"

[Setup]
AppId={{{#MyAppID}}
AppName=leJOS EV3
AppVersion={#MyAppVersion}
AppVerName=leJOS EV3 {#MyAppVersion}
OutputBaseFilename=leJOS_EV3_{#MyAppVersion}_win32_setup
AppPublisher=The leJOS Team
AppPublisherURL=http://www.lejos.org/
AppSupportURL=http://www.lejos.org/
AppUpdatesURL=http://www.lejos.org/
SetupIconFile=../../org.lejos.website/htdocs/lejos.ico
DefaultDirName={pf}\leJOS EV3
DefaultGroupName=leJOS EV3
AllowNoIcons=true
SolidCompression=yes
Compression=lzma2/max
OutputDir=.
ChangesEnvironment=yes
MinVersion=0,5.0
WizardImageFile=img\WizardImage.bmp
WizardImageStretch=no
WizardImageBackColor=clWhite
WizardSmallImageFile=img\WizardSmallImage.bmp
UninstallFilesDir={app}\uninst

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[CustomMessages]
LaunchProgram=Launch EV3SDCard utility
JDKSelectCaption=Select a Java Development Kit
JDKSelectDescription=Select a Java Development Kit for use with leJOS EV3

[Types]
Name: "compact"; Description: "Compact installation"
Name: "minimal"; Description: "Minimal installation"
Name: "full"; Description: "Full installation"
Name: "custom"; Description: "Custom installation"; Flags: iscustom
  
[Components]
Name: "main"; Description: "leJOS EV3 Development Kit"; Types: full compact minimal custom; Flags: fixed disablenouninstallwarning
Name: "docs"; Description: "Documentation"; Types: full compact; Flags: disablenouninstallwarning
Name: "docs\apiev3"; Description: "API Documentation (EV3)"; Types: full compact; Flags: disablenouninstallwarning
Name: "docs\apipc"; Description: "API Documentation (PC)"; Types: full compact; Flags: disablenouninstallwarning
Name: "extras"; Description: "Additional Sources"; Types: full; Flags: disablenouninstallwarning
Name: "extras\samples"; Description: "Sample and Example Projects"; Types: full; Flags: disablenouninstallwarning
Name: "extras\sources"; Description: "Sources of leJOS EV3 Development Kit"; Types: full; Flags: disablenouninstallwarning

[Files]
; Extract helper script to {app}, since {tmp} refers to the temp folder of the admin, and might
; not even be accessible by the original user when using postinstall/runasoriginaluser in [Run]
Source: "scripts\startEV3SDCard.bat"; DestDir: "{app}"; Flags: deleteafterinstall
Source: "..\ev3release\build\bin_windows\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs; Components: main
Source: "..\ev3release\build\bin_windows\docs\ev3\*"; DestDir: "{app}\docs\ev3"; Flags: ignoreversion recursesubdirs createallsubdirs; Components: docs\apiev3
Source: "..\ev3release\build\samples\*"; DestDir: "{code:ExtrasDirPage_GetSamplesFolder}"; Flags: ignoreversion recursesubdirs createallsubdirs; Components: extras\samples
Source: "..\ev3release\build\source\*"; DestDir: "{code:ExtrasDirPage_GetSourcesFolder}"; Flags: ignoreversion recursesubdirs createallsubdirs; Components: extras\sources

[Icons]
Name: "{group}\API Documentation (EV3)"; Filename: "{app}\docs\ev3\index.html"; Components: docs\apiev3
Name: "{group}\EV3 Control"; Filename: "{app}\bin\ev3control.bat"; Flags: closeonexit runminimized
Name: "{group}\EV3 Image Convertor"; Filename: "{app}\bin\ev3image.bat"; Flags: closeonexit runminimized
Name: "{group}\EV3 Map Command"; Filename: "{app}\bin\ev3mapcommand.bat"; Flags: closeonexit runminimized

Name: "{group}\Uninstall LeJOS"; Filename: "{uninstallexe}"

[Registry]
; Delete LEJOS_EV3_JAVA_HOME and EV3_HOME value for current user and set new value globally
Root: HKCU; Subkey: "Environment"; ValueType: none; ValueName: "EV3_HOME"; Flags: deletevalue
Root: HKCU; Subkey: "Environment"; ValueType: none; ValueName: "LEJOS_EV3_JAVA_HOME"; Flags: deletevalue

[Run]
; ev3sdcard will terminate immediately, and hence we don't use the nowait flag.
; Not using the nowait flag also makes sure that the batch file can be deleted successfully.
WorkingDir: "{app}"; Filename: "{app}\startEV3SDcard.bat"; Parameters: "{code:JDKSelect_GetSelectionQuoted}"; Description: "{cm:LaunchProgram}"; Flags: postinstall skipifsilent runhidden

[Messages]
FinishedLabel=Setup has finished installing [name] on your computer. The application may be launched by selecting the installed icons. See the leJOS Wiki for how to install the Eclipse plugin and develop programs.
ClickFinish=Insert your SD card into the SD card reader and click Finish to exit Setup and run the EV3SDCard utility.

#include "include\Tools.iss"
#include "include\ModPath.iss"
#include "include\JDKSelect.iss"
#include "include\ExtrasDirPage.iss"
#include "include\UnInstall.iss"

[Code] 
  function JDKSelect_GetSelectionQuoted(Param: String): String;
  begin
    Result := AddQuotes(JDKSelect_GetSelection(Param));
  end;

  procedure CurStepChanged(CurStep: TSetupStep);
  var
    Data: String;
  begin
    if CurStep = ssPostInstall then
    begin
      try
        GetEnvVar('Path', Data);
        SetExpEnvVar('Path', ModPath_Append(Data, ExpandConstant('{app}\bin')));
      except
        ShowExceptionMessage;
      end;
      try
        SetEnvVar('EV3_HOME', ExpandConstant('{app}'));
      except
        ShowExceptionMessage;
      end;
      try
        SetEnvVar('LEJOS_EV3_JAVA_HOME', JDKSelect_GetSelection('dummy param'));
      except
        ShowExceptionMessage;
      end;
    end;   
  end;
  
  procedure CurUninstallStepChanged(CurUninstallStep: TUninstallStep);
  var
    Data: String;
  begin
    if CurUninstallStep = usUninstall then
    begin
      try
        GetEnvVar('Path', Data);
        SetEnvVar('Path', ModPath_Delete(Data, ExpandConstant('{app}\bin')));
      except
        ShowExceptionMessage;
      end;
      try
        DeleteEnvVar('EV3_HOME');
      except
        ShowExceptionMessage;
      end;
      try
        DeleteEnvVar('LEJOS_EV3_JAVA_HOME');
      except
        ShowExceptionMessage;
      end;
    end;   
  end;
  
  function NextButtonClick(curPageID: Integer): Boolean;
  var
    ID : String;
  begin
    
    if curPageID = wpReady then
    begin
      ID := '{#MyAppID}'      
      Result := UninstallInnoSetup(ID);
      if not Result then Exit;     
    end;
    
    Result := true;
  end;
   
  function UpdateReadyMemo(Space, NewLine, MemoUserInfoInfo, MemoDirInfo, MemoTypeInfo,
    MemoComponentsInfo, MemoGroupInfo, MemoTasksInfo: String): String;
  begin
    Result := MemoDirInfo + NewLine;
    if IsComponentSelected('extras\samples') then
    begin
      Result := Result + Space + ExtrasDirPage_GetSamplesFolder('') + NewLine;
    end;
    if IsComponentSelected('extras\sources') then
    begin
      Result := Result + Space + ExtrasDirPage_GetSourcesFolder('') + NewLine;
    end;
    if MemoGroupInfo > '' then
    begin
      Result := Result + NewLine;    
      Result := Result + MemoGroupInfo + NewLine;
    end;
    Result := Result + NewLine;
    Result := Result + MemoTypeInfo + NewLine;
    Result := Result + NewLine;
    Result := Result + MemoComponentsInfo + NewLine;
    // Result := Result + NewLine;    
    // Result := Result + MemoTasksInfo + NewLine;  
  end;

  procedure InitializeWizard();
  begin
    JDKSelect_CreatePage(wpUserInfo);
    ExtrasDirPage_CreatePage(wpSelectComponents);    
  end;

//#expr SaveToFile("debug.iss")
