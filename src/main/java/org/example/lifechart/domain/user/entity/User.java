    package org.example.lifechart.domain.user.entity;

    import jakarta.persistence.*;
    import lombok.*;
    import org.example.lifechart.common.entity.BaseEntity;

    import java.time.LocalDate;
    import java.time.LocalDateTime;

    @Entity
    @Table(name = "users")
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public class User extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true)
        private String email;

        @Column(nullable = false)
        private String password;

        @Column(nullable = false, unique = true)
        private String nickname;

        @Column
        private String gender;

        @Column
        private String phoneNumber;

        @Column
        private String job;

        @Column(nullable = false)
        private Boolean isDeleted = false;

        @Column
        private LocalDateTime deletedAt;

        @Column(nullable = false)
        private LocalDate birthDate;

        public int getAge() {
            if (birthDate == null) return 0; // birthDate가 null일 수 있는 흐름을 대비해서 방어 코드
            return LocalDate.now().getYear() - birthDate.getYear();
        }

        public void softDelete() {
            if (!Boolean.TRUE.equals(this.isDeleted)) {
            this.isDeleted = true;
            this.deletedAt = LocalDateTime.now();
            }
        }

        public void updateProfile(String nickname, String gender, String job, String phoneNumber) {
            if (nickname != null)this.nickname = nickname;
            if (gender != null) this.gender = gender;
            if (job != null) this.job = job;
            if (phoneNumber != null) this.phoneNumber = phoneNumber;
        }

        @Builder.Default
        @Column(nullable = false)
        private String role = "USER";

        //소셜로그인
        @Column
        private String provider;

        @Column
        private String providerId;
    }