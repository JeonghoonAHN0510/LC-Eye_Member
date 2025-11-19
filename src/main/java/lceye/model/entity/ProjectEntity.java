package lceye.model.entity;

import jakarta.persistence.*;
import lceye.model.dto.ProjectDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEntity extends BaseTime{
    // 1. 테이블 생성
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "int unsigned")
    private int pjno;
    @Column(columnDefinition = "varchar(100)")
    private String pjname;
    @Column(columnDefinition = "double not null")
    private double pjamount;
    @Column(columnDefinition = "longtext")
    private String pjdesc;
    @Column(columnDefinition = "char(40)")
    private String pjfilename;
    @ManyToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "mno", columnDefinition = "int unsigned not null")
    private MemberEntity memberEntity;
    @ManyToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "uno", columnDefinition = "int unsigned")
    private UnitsEntity unitsEntity;
    // 2. toDto 생성
    public ProjectDto toDto(){
        return ProjectDto.builder()
                .pjno(this.pjno)
                .pjname(this.pjname)
                .pjamount(this.pjamount)
                .pjdesc(this.pjdesc)
                .pjfilename(this.pjfilename)
                .mno(this.memberEntity.getMno())
                .uno(this.unitsEntity.getUno())
                .mname(this.memberEntity.getMname())
                .createdate(this.getCreatedate().toString())
                .updatedate(this.getUpdatedate().toString())
                .build();
    } // func end
} // class end