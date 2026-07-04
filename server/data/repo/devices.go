package repo

import (
	"errors"
	"mdcs-server/data"
	"mdcs-server/models"
)

func AddDevice(wid string, device models.DeviceAttrs) {
	data.Store.Devices[wid] = append(data.Store.Devices[wid], device)
}

func DeviceByName(wid, dname string) (models.DeviceAttrs, error) {

	for _, device := range data.Store.Devices[wid] {

		if device.DName == dname {
			return device, nil
		}
	}

	return models.DeviceAttrs{}, errors.New("DEVICE_NOT_FOUND")
}

func DeviceById(wid, did string) (models.DeviceAttrs, error) {

	for _, device := range data.Store.Devices[wid] {

		if device.DId == did {
			return device, nil
		}
	}

	return models.DeviceAttrs{}, errors.New("DEVICE_NOT_FOUND")
}
