package disa.notification.service.service.impl;

import disa.notification.service.repository.ViralLoaderRepository;
import disa.notification.service.service.interfaces.ViralLoaderResult;
import disa.notification.service.service.interfaces.ViralLoaderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ViralLoaderServiceImpl  implements ViralLoaderService {

    private final ViralLoaderRepository viralLoaderRepository;

    @Override
    public  List<ViralLoaderResult> findTop10ViralLoaders() {
        return viralLoaderRepository.findViralLoadResult();
    }
}
