package version

import (
	"mdcs-server/api/shared"
	"mdcs-server/core/bootstrap"
)

func responseBuilder(update_check_data UpdateCheckRequest) (UpdateCheckResponse, error) {
	var response UpdateCheckResponse

	app, err := criticalUpdateCheck(update_check_data)
	if err != nil {
		return UpdateCheckResponse{}, err
	}

	plugins, err := pluginCompatAndUpCheck(update_check_data)
	if err != nil {
		return UpdateCheckResponse{}, err
	}

	response.App = app
	response.Plugins = plugins
	response.Changes = bootstrap.Metadata.Changes

	return response, nil
}

func criticalUpdateCheck(update_check_data UpdateCheckRequest) (AppData, error) {
	curr_app_ver, err := shared.ParseSemVer(update_check_data.App["current_version"])
	if err != nil {
		return AppData{}, err
	}

	min_sup_ver, err := shared.ParseSemVer(bootstrap.Metadata.App.MinimumSupportedVersion)
	if err != nil {
		return AppData{}, err
	}

	var app_data AppData
	app_data.CurrentVersion = update_check_data.App["current_version"]
	app_data.AvailableVersion = bootstrap.Metadata.App.LatestVersion
	app_data.CriticalUpdate = shared.IsLowerVersion(curr_app_ver, min_sup_ver)

	return app_data, nil
}

func pluginCompatAndUpCheck(update_check_data UpdateCheckRequest) (map[string]PluginData, error) {
	plugins := make(map[string]PluginData)

	curr_app_semver, err := shared.ParseSemVer(update_check_data.App["current_version"])
	if err != nil {
		return nil, err
	}

	for plugin_name, installed_ver := range update_check_data.Plugins {
		var plugin_res PluginData

		plugin_meta, exists := bootstrap.Metadata.Plugins[plugin_name]

		if !exists {
			continue
		}

		installed_semver, err := shared.ParseSemVer(installed_ver)
		if err != nil {
			return nil, err
		}

		available_semver, err := shared.ParseSemVer(plugin_meta.AvailableVersion)
		if err != nil {
			return nil, err
		}

		min_compat_semver, err := shared.ParseSemVer(plugin_meta.CompatibleAppVersions.Min)
		if err != nil {
			return nil, err
		}

		max_compat_semver, err := shared.ParseSemVer(plugin_meta.CompatibleAppVersions.Max)
		if err != nil {
			return nil, err
		}

		plugin_res.InstalledVersion = installed_ver
		plugin_res.AvailableVersion = plugin_meta.AvailableVersion
		plugin_res.UpdateRequired = shared.IsLowerVersion(installed_semver, available_semver)
		plugin_res.IsCompatible = shared.IsVerInRange(curr_app_semver, min_compat_semver, max_compat_semver)

		plugins[plugin_name] = plugin_res
	}

	return plugins, nil
}
