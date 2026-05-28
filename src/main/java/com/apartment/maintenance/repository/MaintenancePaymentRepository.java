package com.apartment.maintenance.repository;

import com.apartment.maintenance.dto.DefaulterResponse;
import com.apartment.maintenance.dto.MyDueResponse;
import com.apartment.maintenance.entity.MaintenancePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaintenancePaymentRepository
        extends JpaRepository<MaintenancePayment, UUID> {

    List<MaintenancePayment> findBySiteId(UUID siteId);

    List<MaintenancePayment> findByFlatId(UUID flatId);

    List<MaintenancePayment> findByRequestId(UUID requestId);

   /* Optional<MaintenancePayment>
    findByFlatIdAndPaymentMonthAndPaymentYear(
            UUID flatId,
            Integer paymentMonth,
            Integer paymentYear
    );*/

    List<MaintenancePayment>
    findBySiteIdAndPaymentMonthAndPaymentYear(
            UUID siteId,
            Integer paymentMonth,
            Integer paymentYear
    );


    boolean existsByFlatIdAndPaymentMonthAndPaymentYearAndRequestType(
            UUID flatId,
            Integer paymentMonth,
            Integer paymentYear,
            String requestType
    );

    List<MaintenancePayment>
    findByFlatIdAndPaymentStatus(UUID flatId, String status);

    List<MaintenancePayment>
    findByPaymentStatus(String status);

    long countBySiteId(UUID siteId);

    long countBySiteIdAndPaymentStatus(
            UUID siteId,
            String paymentStatus
    );

    Optional<MaintenancePayment> findByPaymentId(UUID paymentId);
    List<MaintenancePayment>
    findByFlatIdAndPaymentStatusOrderByPaymentYearDescPaymentMonthDesc(
            UUID flatId,
            String paymentStatus
    );

    @Query(value = """
    SELECT
        f.flat_number AS flatNumber,
        f.owner_name AS ownerName,

        COUNT(DISTINCT CASE
            WHEN mp.request_type = 'Maintenance'
            THEN CONCAT(mp.payment_year, '-', mp.payment_month)
        END) AS maintenancePendingMonths,

        COALESCE(SUM(CASE
            WHEN mp.request_type = 'Maintenance'
            THEN mp.amount ELSE 0
        END), 0) AS maintenanceDue,

        COUNT(CASE
            WHEN mp.request_type = 'Special Request'
            THEN 1
        END) AS specialRequestCount,

        COALESCE(SUM(CASE
            WHEN mp.request_type = 'Special Request'
            THEN mp.amount ELSE 0
        END), 0) AS specialRequestDue,

        COUNT(CASE
            WHEN mp.request_type NOT IN ('Maintenance', 'Special Request')
                 OR mp.request_type IS NULL
            THEN 1
        END) AS otherPendingCount,

        COALESCE(SUM(CASE
            WHEN mp.request_type NOT IN ('Maintenance', 'Special Request')
                 OR mp.request_type IS NULL
            THEN mp.amount ELSE 0
        END), 0) AS otherDue,

        COALESCE(SUM(mp.amount), 0) AS totalDue

    FROM maintenance_payments mp
    JOIN flats f ON mp.flat_id = f.flat_id

    WHERE mp.site_id = :siteId
      AND UPPER(mp.payment_status) <> 'PAID'

    GROUP BY f.flat_number, f.owner_name

    HAVING COALESCE(SUM(mp.amount), 0) > 0

    ORDER BY totalDue DESC
""", nativeQuery = true)
    List<DefaulterResponse> findDefaulters(UUID siteId);

    @Query("""
    SELECT COALESCE(SUM(mp.amount),0)
    FROM MaintenancePayment mp
    WHERE mp.siteId = :siteId
      AND mp.paymentStatus = 'PAID'
      AND mp.paymentMonth = :month
      AND mp.paymentYear = :year
""")
    Double getMonthlyCollections(
            UUID siteId,
            String month,
            String year);

    @Query("""
    SELECT COALESCE(SUM(mp.amount),0)
    FROM MaintenancePayment mp
    WHERE mp.siteId = :siteId
      AND mp.paymentStatus='PAID'
      AND (
            mp.paymentYear < :year OR
            (mp.paymentYear = :year AND mp.paymentMonth < :month)
      )
""")
    Double getCollectionsBefore(UUID siteId,
                                String month,
                                String year);

    @Query(value = """
SELECT
    mp.payment_id,
    mp.payment_month,
    mp.payment_year,
    mp.amount,
    mp.payment_status,
    mp.payment_date,
    mp.created_at
FROM maintenance_payments mp
WHERE mp.flat_id = :flatId
ORDER BY mp.payment_year, mp.payment_month
""", nativeQuery = true)
    List<Object[]> getFlatStatement(UUID flatId);

    @Query(value = """
    SELECT COALESCE(SUM(amount),0)
    FROM maintenance_payments
    WHERE flat_id = :flatId
      AND payment_status <> 'PAID'
""", nativeQuery = true)
    BigDecimal getPendingAmount(UUID flatId);

    @Query(value = """
    SELECT COALESCE(SUM(amount),0)
    FROM maintenance_payments
    WHERE flat_id = :flatId
      AND payment_status = 'PAID'
""", nativeQuery = true)
    BigDecimal getTotalPaid(UUID flatId);

    @Query(value = """
    SELECT COUNT(*)
    FROM maintenance_payments
    WHERE flat_id = :flatId
      AND payment_status <> 'PAID'
""", nativeQuery = true)
    Long getPendingMonths(UUID flatId);

    @Query(value = """
    SELECT MAX(payment_date)
    FROM maintenance_payments
    WHERE flat_id = :flatId
      AND payment_status = 'PAID'
""", nativeQuery = true)
    LocalDateTime getLastPaymentDate(UUID flatId);


    @Query(value = """
    SELECT user_id
    FROM users
    WHERE flat_id = :flatId
""", nativeQuery = true)
    UUID getUserUUID(UUID flatId);

    List<MaintenancePayment> findByFlatIdAndPaymentMonthAndPaymentYear(
            UUID flatId,
            Integer paymentMonth,
            Integer paymentYear
    );

    @Query(value = """
select
mp.payment_id,
mp.flat_id,
fl.flat_number,
mp.amount,
mp.payment_month,
mp.payment_year,
mp.payment_status,
mp.payment_mode,
mp.request_type,
mp.receipt_url,
mp.created_at
from maintenance_payments mp
join flats fl
on fl.flat_id = mp.flat_id
where mp.payment_status='SUBMITTED'
and mp.site_id=:siteId
order by mp.created_at desc
""", nativeQuery = true)
    List<Object[]> getPendingApprovals(UUID siteId);

    List<MaintenancePayment>
    findByFlatIdAndPaymentStatusNot(
            UUID flatId,
            String status
    );

    @Query(value = """
        select count(distinct flat_id)
        from maintenance_payments
        where site_id = :siteId
        and payment_month = :month
        and payment_year = :year
        and request_type = 'Maintenance'
        and payment_status = 'PAID'
        """, nativeQuery = true)
    long countPaidMaintenanceFlats(UUID siteId, Integer month, Integer year);

    @Query(value = """
        select coalesce(sum(amount), 0)
        from maintenance_payments
        where site_id = :siteId
        and payment_month = :month
        and payment_year = :year
        and request_type = 'Special Request'
        and payment_status = 'PAID'
        """, nativeQuery = true)
    BigDecimal getSpecialRequestCollected(
            UUID siteId,
            Integer month,
            Integer year
    );

    @Query(value = """
select
mp.payment_id,
mp.flat_id,
fl.flat_number,
mp.amount,
mp.payment_month,
mp.payment_year,
mp.payment_status,
mp.payment_mode,
mp.request_type,
mp.receipt_url,
mp.created_at
from maintenance_payments mp
join flats fl on fl.flat_id = mp.flat_id
where mp.payment_status = 'SUBMITTED'
and mp.flat_id = :flatId
order by mp.created_at desc
""", nativeQuery = true)
    List<Object[]> getPendingApprovalsByFlatId(UUID flatId);

    @Query(value = """
        select
            mp.created_at as entry_date,
            concat(mp.request_type, ' Charge - ', mp.payment_month, '/', mp.payment_year) as description,
            mp.amount as debit,
            0 as credit,
            null as receipt_pdf_url
        from maintenance_payments mp
        where mp.flat_id = :flatId

        union all

        select
            coalesce(mp.payment_date, mp.approved_at, mp.created_at) as entry_date,
            concat('Payment Received - ', coalesce(mp.payment_mode, '-')) as description,
            0 as debit,
            mp.amount as credit,
            mp.receipt_pdf_url as receipt_pdf_url
        from maintenance_payments mp
        where mp.flat_id = :flatId
        and mp.payment_status = 'PAID'

        order by entry_date asc
        """, nativeQuery = true)
    List<Object[]> getFlatStatementEntries(UUID flatId);

    @Query(value = """
        select mp.*
        from maintenance_payments mp
        join payment_requests pr
            on pr.request_id = mp.request_id
        join scheduled_payment_requests spr
            on spr.site_id = mp.site_id
            and spr.active = true
        where mp.payment_status in ('PENDING', 'REJECTED')
        and pr.request_type = 'Maintenance'
        and pr.due_date >= current_date
        and (
            mp.last_reminder_sent_at is null
            or mp.last_reminder_sent_at <= now() - (spr.reminder_frequency_days || ' days')::interval
        )
        """, nativeQuery = true)
    List<MaintenancePayment> findPaymentsForDueReminder();

    @Query("""
    SELECT new com.apartment.maintenance.dto.MyDueResponse(
        mp.paymentId,
        mp.siteId,
        mp.flatId,
        mp.paymentMonth,
        mp.paymentYear,
        mp.amount,
        mp.paymentStatus,
        mp.paymentMode,
        mp.paymentDate,
        mp.approvedAt,
        mp.receiptUrl,
        mp.createdAt,
        mp.requestId,
        mp.requestType,
        pr.title,
        pr.description,
        mp.receiptNumber,
        mp.receiptPdfUrl
    )
    FROM MaintenancePayment mp
    LEFT JOIN PaymentRequest pr
        ON pr.requestId = mp.requestId
    WHERE mp.flatId = :flatId
      AND mp.paymentStatus <> 'PAID'
    ORDER BY mp.paymentYear DESC, mp.paymentMonth DESC, mp.createdAt DESC
""")
    List<MyDueResponse> findMyDuesWithRequestDetails(UUID flatId);
}
