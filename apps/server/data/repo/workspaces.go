package repo

import (
	"errors"
	"mdcs-server/data"
	"mdcs-server/models"
)

func AddWorkspace(uid string, workspace models.WorkspaceAttrs) {
	data.Store.Workspaces[uid] = append(data.Store.Workspaces[uid], workspace)
}

func DeleteWorkspace(uid, wid string) error {

	for i, ws := range data.Store.Workspaces[uid] {

		if ws.WId == wid {
			data.Store.Workspaces[uid] = append(
				data.Store.Workspaces[uid][:i],
				data.Store.Workspaces[uid][i+1:]...,
			)

			return nil
		}
	}

	// If nothing returned till now, then there is no such workspace
	return nil
}

func WorkspaceByName(uid, wname string) (models.WorkspaceAttrs, error) {

	for _, ws := range data.Store.Workspaces[uid] {

		if ws.WName == wname {
			return ws, nil
		}
	}

	return models.WorkspaceAttrs{}, errors.New("WORKSPACE_NOT_FOUND")
}

func WorkspaceById(uid, wid string) (models.WorkspaceAttrs, error) {
	workspaces := data.Store.Workspaces[uid]

	for _, workspace := range workspaces {

		if workspace.WId == wid {
			return workspace, nil
		}
	}

	return models.WorkspaceAttrs{}, errors.New("WORKSPACE_NOT_FOUND")
}
