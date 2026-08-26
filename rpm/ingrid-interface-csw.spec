Name:                       ingrid-interface-csw
Version:                    0.0.0
Release:                    dev
Summary:                    InGrid Interface CSW
Group:                      Applications/Internet
License:                    Proprietary
URL:                        https://www.wemove.com/
BuildArch:                  noarch
AutoReqProv:                no
Requires:                   jre >= 25

%define target              %{buildroot}/opt/ingrid/ingrid-interface-csw
%define systemd_dir         /usr/lib/systemd/system
%define ingrid_unit_name    ingrid-interface-csw.service
%define ingrid_service      %{systemd_dir}/%{ingrid_unit_name}

%description
InGrid Interface CSW

%prep

unzip -qq "${WORKSPACE}/target/ingrid-interface-csw-[0-9]*-installer.jar"

%build
# nothing to do

%install

# Clean up files from previous builds
# !!! Some paths have leading slashes and don't need a slash after
# the buildroot macro.
rm -Rf %{buildroot}*

# Create destination directory and copy files over

# interface-csw
mkdir -p %{target}/logs
mv ./ingrid-interface-csw-*/* %{target}

# Copy over the systemd unit file
mkdir -p %{buildroot}%{systemd_dir}
cp ${WORKSPACE}/rpm/%{ingrid_unit_name} %{buildroot}%{systemd_dir}

%files
%defattr(0644,ingrid,ingrid,0755)
%attr(0755,ingrid,ingrid) /opt/ingrid/ingrid-interface-csw
%attr(0644,root,root) %{ingrid_service}

################################################################################
%pre
# Scriptlet that is executed just before the package is installed on the target
# system.
if [ -f "/etc/systemd/system/ingrid-interface-csw.service" ]; then
  service ingrid-interface-csw stop
fi

# Delete old files and libs
for dir in %{install_root}/%{ingrid_name}/conf \
    %{install_root}/%{ingrid_name}/lib \
    %{install_root}/%{ingrid_name}/logs; do

# Don't use `test' here. If the last directory doesn't exist, then a non-zero
# exit code from test will cause the installation to fail.
    if [ -d "$dir" ]; then
        rm -Rf "$dir"/*
    fi
done

################################################################################
%preun
if [ -f "/etc/systemd/system/ingrid-interface-csw.service" ]; then
  service ingrid-interface-csw stop
fi

################################################################################
%postun


%changelog
