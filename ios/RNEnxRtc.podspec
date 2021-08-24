
require 'json'

package = JSON.parse(File.read(File.join(__dir__, '../package.json')))

Pod::Spec.new do |s|
  s.name         = "enx-rtc-react-native"
  s.version      = "1.5.4"
  s.summary      = "react-native Toolkit"
  s.license      = { :type => 'Vcloudx License, Version 1.1', :text => 'https://www.enablex.io/legals/tou/'}
  s.authors      = { 'enablex' => 'https://github.com/enablexer' }
  s.homepage     = 'https://developer.enablex.io/api/client-api/ios-toolkit/'
  s.platform     = :ios, "10.0"

s.source       = { :git => "https://github.com/EnableX/enx-rtc-react-native.git", :tag => "master" }

  
  s.source_files  = "**/*.{h,m,swift}"
  s.requires_arc = true

  s.dependency "React"
  s.dependency "EnxRTCiOS", '1.6.0'
  s.dependency 'Socket.IO-Client-Swift', '~> 15.0.0'
end

