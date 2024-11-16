package com.dz.coop.module.service;


import com.dz.coop.module.model.cp.CPVolume;

/**
 * @author panqz 2018-12-11 11:35 PM
 */

public interface VolumeService {
    Long saveVolums(CPVolume cpVolumes, String bookId);
}
