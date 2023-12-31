import { Inject, Injectable } from '@angular/core';
import { APP_SERVICE_CONFIG, AppConfig } from '../../app-config/app-config';
import { HttpClient } from '@angular/common/http';
import { CSR } from '../model/CSR.model';
import { CertificateType } from '../model/certificate-type.model';
import { Extension } from '../model/extension.model';
import { Capabilities } from '../model/capabilities.model';
import { Certificate } from '../model/certificate.model';

@Injectable({
  providedIn: 'root',
})
export class CertificatesAdminHttpService {
  private GET_PENDING_CSRS = 'csr/pending';
  private REJECT_CSR = 'csr/reject';
  private GET_CERTIFICATE_TYPES = 'certificate/types';
  private GET_CERTIFICATE_EXTENSIONS = 'certificate/extensions';
  private CREATE_CERTIFICATE = 'certificate';
  private GET_CERTIFICATES = 'certificate/all';
  private REVOKE_CERTIFICATE = 'certificate/revoke';

  constructor(
    @Inject(APP_SERVICE_CONFIG) private config: AppConfig,
    private http: HttpClient
  ) {}

  getPendingCSRs() {
    return this.http.get<CSR[]>(
      this.config.apiEndpoint + this.GET_PENDING_CSRS
    );
  }

  rejectCSR(id: string, reason: string) {
    return this.http.put(this.config.apiEndpoint + this.REJECT_CSR, {
      id,
      reason,
    });
  }

  getCertificateTypes() {
    return this.http.get<CertificateType[]>(
      this.config.apiEndpoint + this.GET_CERTIFICATE_TYPES
    );
  }

  getCertificateExtensions() {
    return this.http.get<Extension[]>(
      this.config.apiEndpoint + this.GET_CERTIFICATE_EXTENSIONS
    );
  }

  sendCreateCertificateRequest(
    capabilities: Capabilities[],
    start: Date,
    end: Date,
    csrId: string,
    serialNumber: string,
    caAlias: string
  ) {
    return this.http.post(this.config.apiEndpoint + this.CREATE_CERTIFICATE, {
      capabilities,
      start,
      end,
      csrId,
      serialNumber,
      caAlias,
    });
  }

  getCertificates() {
    return this.http.get<Certificate[]>(
      this.config.apiEndpoint + this.GET_CERTIFICATES
    );
  }

  revokeCertificate(alias: string, message: string, reason: number) {
    return this.http.put(this.config.apiEndpoint + this.REVOKE_CERTIFICATE, {
      alias,
      message,
      reason,
    });
  }
}
