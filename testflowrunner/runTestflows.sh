#!/bin/bash

set -e

# Take the execution time
START_TIME=`date +%s`

# Change to script directory
cd "$(dirname "$0")"

# Set environment variables
KNIME_VERSION_MAJOR=3
KNIME_VERSION_MINOR=6
KNIME_VERSION_PATCH=0

# Platform-specific KNIME download and installation
KNIME_VERSION=${KNIME_VERSION_MAJOR}.${KNIME_VERSION_MINOR}.${KNIME_VERSION_PATCH}

# Update site with the Cyface Nodes (relative path)
# https://unix.stackexchange.com/a/156287
PATTERN="../p2/target/de.cyface.knime.p2-*.zip"
FILES=( $PATTERN )
NODES_UPDATE_SITE="${FILES[0]}"

# Path where the downloaded KNIME is stored
DOWNLOAD_CACHE="./knime-download"
mkdir -p "${DOWNLOAD_CACHE}"

# Temporary directory where we put the KNIME installation
TEMP_DIR=$(mktemp -d)

download_if_not_cached() {
  DESTINATION_FILE="$1";
  DOWNLOAD_URL="$2";
  if [ ! -f "${DESTINATION_FILE}" ]
  then
    # write to 'file.download' first, and only rename to
    # target filename when download finished successfully
    curl -o "${DESTINATION_FILE}.download" "${DOWNLOAD_URL}" &&
    mv "${DESTINATION_FILE}.download" "${DESTINATION_FILE}"
  else
    echo "Using cached file ${DESTINATION_FILE} -- in case something’s wrong: delete this and retry"
  fi
}

cleanup() {
  rm -rf "${TEMP_DIR}"
  echo "Deleted temporary directory ${TEMP_DIR}"
}
trap cleanup EXIT

# https://stackoverflow.com/a/21188136
get_absolute_file_path() {
  # $1 : relative filename
  echo "$(cd "$(dirname "$1")" && pwd)/$(basename "$1")"
}

case "$OSTYPE" in

  linux*)
    DOWNLOAD_FILE="${DOWNLOAD_CACHE}/knime_${KNIME_VERSION}.tar.gz"
    download_if_not_cached "${DOWNLOAD_FILE}" "https://download.knime.org/analytics-platform/linux/knime_${KNIME_VERSION}.linux.gtk.x86_64.tar.gz"
    tar -zxf "${DOWNLOAD_FILE}" -C "${TEMP_DIR}"
    KNIME_APPLICATION="${TEMP_DIR}/knime_${KNIME_VERSION}/knime"
    ;;

  darwin*)
    DOWNLOAD_FILE="${DOWNLOAD_CACHE}/knime_${KNIME_VERSION}.dmg"
    download_if_not_cached "${DOWNLOAD_FILE}" "https://download.knime.org/analytics-platform/macosx/knime_${KNIME_VERSION}.app.macosx.cocoa.x86_64.dmg"
    # mount the disk image and extract the KNIME.app file
    # https://stackoverflow.com/a/22067126
    echo "Extracting KNIME.app from disk image"
    VOLUME=$(hdiutil attach "${DOWNLOAD_FILE}" | tail -1 | awk '{$1=$2=""; gsub(/^ +/,"",$0); print $0}')
    cp -r "${VOLUME}/KNIME ${KNIME_VERSION}.app" "${TEMP_DIR}/KNIME.app"
    diskutil unmount "$VOLUME"
    KNIME_APPLICATION="${TEMP_DIR}/KNIME.app/Contents/MacOS/Knime"
    ;;

  *)
    echo "Unsupported operating system $OSTYPE"
    exit 1
    ;;

esac

# Install Testing Nodes
"${KNIME_APPLICATION}" -clean -purgeHistory -application org.eclipse.equinox.p2.director -noSplash \
  -repository http://update.knime.org/analytics-platform/${KNIME_VERSION_MAJOR}.${KNIME_VERSION_MINOR} \
  -installIUs org.knime.features.testingapplication.feature.group,org.knime.features.base.filehandling.feature.group  \
  -vmargs -Declipse.p2.mirrors=true -Djava.net.preferIPv4Stack=true

# Install the Cyface Nodes build from local directory
# TODO spaces would need to be converted to %20
# https://wiki.eclipse.org/Equinox/p2/Zipped_Repos#Headless:_Provisioning_your_install_from_a_zipped_p2_repo
ABSOLUTE_FILE_URL_TO_UPDATE_SITE_ZIP="jar:file:$(get_absolute_file_path ${NODES_UPDATE_SITE})!"
"${KNIME_APPLICATION}" -clean -purgeHistory -application org.eclipse.equinox.p2.director -noSplash \
  -repository $ABSOLUTE_FILE_URL_TO_UPDATE_SITE_ZIP \
  -installIUs de.cyface.knime.feature.feature.group \
  -vmargs -Declipse.p2.mirrors=true -Djava.net.preferIPv4Stack=true

# Clean up the testflow reports
TESTFLOW_REPORTS="${PWD}/testflow-reports"
rm -rf "$TESTFLOW_REPORTS" || true

# This option must only be set when running on macOS,
# on other platforms it produces errors
if [[ "$OSTYPE" == darwin* ]]; then LAUNCH_OPTS="-XstartOnFirstThread"; fi

# Run the testflows
eval "${KNIME_APPLICATION}" -noSplash -consoleLog -application org.knime.testing.NGTestflowRunner \
  -root "${PWD}/testflows" \
  -xmlResultDir "${TESTFLOW_REPORTS}" \
  -views \
  -logMessages \
  -loadSaveLoad \
  -data "${TEMP_DIR}/knime-workspace" \
  -vmargs -ea -Dlog4j.configuration="${PWD}/log4j.xml" "${LAUNCH_OPTS}"

# The org.knime.testing.NGTestflowRunner will return
# status 0, even if there are test failures -- so we
# use the test-parser-plugin to check the reports
# and produce an error if applicable
mvn de.philippkatz.maven.plugins:test-parser-plugin:3.1.0:testparser \
  -Dtestparser.resultsDirectory=./testflow-reports

END_TIME=`date +%s`
echo "Running testflows took $((END_TIME-START_TIME)) seconds."
